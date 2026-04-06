# Order Service

## Description
Handles customer orders and orchestration.

## Features
- Order creation
- Stores data in MongoDB
- Implements simulated failure for retry testing
- Sends retry jobs to Kafka topic `order_retry_jobs`.

## Port
- Default: `8082`

## Endpoints
- `POST /ordenes`: Create an order
- `POST /ordenes/retry`: Endpoint for Broker Service to retry order saving.
