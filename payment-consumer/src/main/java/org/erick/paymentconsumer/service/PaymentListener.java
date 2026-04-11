package org.erick.paymentconsumer.service;

import java.time.Instant;
import java.util.UUID;

import org.erick.paymentconsumer.messaging.RabbitMqConstants;
import org.erick.paymentconsumer.messaging.event.OrderCreatedEvent;
import org.erick.paymentconsumer.messaging.event.PaymentProcessedEvent;
import org.erick.paymentconsumer.messaging.status.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final ProcessedEventRegistry processedEventRegistry;

    public PaymentListener(RabbitTemplate rabbitTemplate, ProcessedEventRegistry processedEventRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.processedEventRegistry = processedEventRegistry;
    }

    @RabbitListener(queues = RabbitMqConstants.PAYMENT_QUEUE)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Recebido pedido {} para pagamento com eventId={}", event.orderId(), event.eventId());

        if (processedEventRegistry.isProcessed(event.eventId())) {
            log.info("Mensagem duplicada ignorada para eventId={}", event.eventId());
            return;
        }

        if (event.simulatePaymentFailure()) {
            log.error("Falha simulada no pagamento do pedido {}", event.orderId());
            throw new IllegalStateException("Falha simulada para testar retry e DLQ");
        }

        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
                UUID.randomUUID().toString(),
                event.orderId(),
                event.customerId(),
                event.amount(),
                PaymentStatus.APPROVED,
                Instant.now()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConstants.ORDER_EVENTS_EXCHANGE,
                RabbitMqConstants.PAYMENT_PROCESSED_ROUTING_KEY,
                paymentProcessedEvent
        );
        processedEventRegistry.markProcessed(event.eventId());

        log.info("Pagamento aprovado para pedido {}. Evento publicado para notificacao.", event.orderId());
    }
}
