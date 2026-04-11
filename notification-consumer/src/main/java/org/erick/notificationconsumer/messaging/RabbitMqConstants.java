package org.erick.notificationconsumer.messaging;

public final class RabbitMqConstants {

    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    private RabbitMqConstants() {
    }
}
