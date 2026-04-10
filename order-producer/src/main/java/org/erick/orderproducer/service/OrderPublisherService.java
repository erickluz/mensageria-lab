package org.erick.orderproducer.service;

import java.time.Instant;
import java.util.UUID;

import org.erick.orderproducer.dto.CreateOrderRequest;
import org.erick.orderproducer.dto.OrderResponse;
import org.erick.shared.event.OrderCreatedEvent;
import org.erick.shared.status.OrderStatus;
import org.erick.shared.util.RabbitMqConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderPublisherService {

    private static final Logger log = LoggerFactory.getLogger(OrderPublisherService.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();

        OrderCreatedEvent event = new OrderCreatedEvent(
                eventId,
                orderId,
                request.customerId(),
                request.amount(),
                Instant.now(),
                request.simulatePaymentFailure()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConstants.ORDER_EVENTS_EXCHANGE,
                RabbitMqConstants.ORDER_CREATED_ROUTING_KEY,
                event
        );

        log.info("Pedido {} publicado para processamento de pagamento com eventId={}", orderId, eventId);

        return new OrderResponse(
                orderId,
                eventId,
                request.customerId(),
                request.amount(),
                OrderStatus.PAYMENT_PENDING,
                "Pedido recebido e publicado no RabbitMQ"
        );
    }
}
