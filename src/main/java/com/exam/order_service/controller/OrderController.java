package com.exam.order_service.controller;

import com.exam.order_service.model.Order;
import com.exam.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderRepository orderRepository;
    private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    public OrderController(OrderRepository orderRepository, org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        try {
            log.info("Creating order for user: {}", order.getUsuarioId());
            // Simulate random failure for testing retries
            if (Math.random() < 0.3) {
                throw new RuntimeException("Simulated failure during order creation");
            }
            order.setEstado("PENDIENTE");
            return orderRepository.save(order);
        } catch (Exception e) {
            log.error("Error creating order, sending to retry topic: {}", e.getMessage());
            
            java.util.Map<String, Object> wrappedPayload = new java.util.HashMap<>();
            wrappedPayload.put("data", order);
            wrappedPayload.put("sendEmail", java.util.Map.of("status", "PENDING", "message", ""));
            wrappedPayload.put("updateRetryJobs", java.util.Map.of("status", "PENDING", "message", ""));
            
            kafkaTemplate.send("order_retry_jobs", wrappedPayload);
            throw e;
        }
    }

    @PostMapping("/retry")
    public Order retry(@RequestBody Order order) {
        log.info("Retrying save for order from user: {}", order.getUsuarioId());
        return orderRepository.save(order);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable String id) {
        log.info("Fetching order with id: {}", id);
        return orderRepository.findById(id).orElse(null);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Order> getOrdersByUser(@PathVariable String usuarioId) {
        log.info("Fetching orders for user: {}", usuarioId);
        return orderRepository.findByUsuarioId(usuarioId);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable String id, @RequestBody String status) {
        log.info("Updating status for order {}: {}", id, status);
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setEstado(status);
            return orderRepository.save(order);
        }
        return null;
    }
}
