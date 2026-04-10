package org.erick.notificationconsumer.service;

import org.erick.shared.event.PaymentProcessedEvent;
import org.erick.shared.util.RabbitMqConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = RabbitMqConstants.NOTIFICATION_QUEUE)
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        log.info(
                "Notificacao enviada para cliente {} sobre o pedido {} com status {}",
                event.customerId(),
                event.orderId(),
                event.status()
        );
    }
}
