# Order Microservice

Servicio encargado de la gestión y procesamiento de órdenes de compra.

## Detalles Técnicos
- **Puerto:** 8082
- **Base de Datos:** MongoDB (colección `ordenes`).
- **Logs:** Envía logs al log group `ordenes-log-group` en CloudWatch (LocalStack).

## Endpoints (vía Gateway)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/ordenes` | Crear una nueva orden. |
| `GET` | `/ordenes/{id}` | Obtener una orden por su ID. |
| `GET` | `/ordenes/usuario/{usuarioId}` | Listar órdenes de un usuario específico. |
| `PUT` | `/ordenes/{id}/status` | Actualizar el estado de una orden. |
