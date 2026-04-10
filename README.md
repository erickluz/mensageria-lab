# Mensageria Lab

Laboratorio de mensageria em Java com Maven e RabbitMQ, organizado em modulos:

- `shared`: DTOs, eventos, enums e constantes compartilhadas.
- `order-producer`: API HTTP que recebe pedidos e publica eventos.
- `payment-consumer`: consome pedidos, simula falha, aplica retry e envia para DLQ.
- `notification-consumer`: consome o evento de pagamento processado para notificacao.
- `dashboard`: reservado para uma interface visual futura.

## Subir o RabbitMQ

```bash
docker compose up -d
```

Painel do RabbitMQ:

- URL: `http://localhost:15672`
- Usuario: `guest`
- Senha: `guest`

## Compilar

```bash
mvn clean package -DskipTests
```

## Executar os modulos

```bash
mvn -pl order-producer spring-boot:run
mvn -pl payment-consumer spring-boot:run
mvn -pl notification-consumer spring-boot:run
```

## Criar um pedido

```bash
curl -X POST http://localhost:8080/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"customerId\":\"cliente-1\",\"amount\":150.00,\"simulatePaymentFailure\":false}"
```

Para testar retry + DLQ, envie `simulatePaymentFailure=true`.
