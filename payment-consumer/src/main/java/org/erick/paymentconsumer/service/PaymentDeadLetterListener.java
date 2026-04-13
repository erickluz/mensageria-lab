package org.erick.paymentconsumer.service;

import org.erick.paymentconsumer.messaging.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentDeadLetterListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentDeadLetterListener.class);

    public void onDeadLetter(OrderCreatedEvent event) {
        log.warn("Mensagem enviada para DLQ. orderId={}, eventId={}", event.orderId(), event.eventId());
    }
}
