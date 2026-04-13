package org.erick.paymentconsumer.service;

import java.time.Instant;
import java.util.UUID;

import org.erick.paymentconsumer.messaging.RabbitMqConstants;
import org.erick.paymentconsumer.messaging.event.OrderCreatedEvent;
import org.erick.paymentconsumer.messaging.event.PaymentProcessedEvent;
import org.erick.paymentconsumer.messaging.status.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
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
    public void onOrderCreated(OrderCreatedEvent event, Message message) {
        log.info("Recebido pedido {} para pagamento com eventId={}", event.orderId(), event.eventId());

        // idempotencia: verifica se o evento já foi processado para evitar processamento duplicado
        if (processedEventRegistry.isProcessed(event.eventId())) {
            log.info("Mensagem duplicada ignorada para eventId={}", event.eventId());
            return;
        }

        if (event.simulatePaymentFailure()) {

            int retryCount = getRetryCount(message, RabbitMqConstants.PAYMENT_RETRY_QUEUE) + 1;
            log.info("Retry count: {}", retryCount);

            if (retryCount > 3) {
                log.error("Numero maximo de retries atingido para eventId={}. Enviando para DLQ.", event.eventId());
                rabbitTemplate.convertAndSend(
                        RabbitMqConstants.DEAD_LETTER_EXCHANGE,
                        RabbitMqConstants.PAYMENT_DLQ_ROUTING_KEY,
                        event
                );
                return;
            } else {
                log.error("Publicando evento de retry para eventId={}", event.eventId());
                rabbitTemplate.convertAndSend(
                        RabbitMqConstants.ORDER_EVENTS_EXCHANGE,
                        RabbitMqConstants.PAYMENT_RETRY_ROUTING_KEY,
                        event,
                    msg -> {
                        msg.getMessageProperties().getHeaders().put("retry-count", retryCount);
                        return msg;
                    }
                );
            }
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

    private int getRetryCount(Message message, String retryQueueName) {
        Object value = message.getMessageProperties().getHeaders().get("retry-count");
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }
}
