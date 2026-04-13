package org.erick.paymentconsumer.messaging;

public final class RabbitMqConstants {

    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String DEAD_LETTER_EXCHANGE = "order.events.dlx";

    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    public static final String PAYMENT_RETRY_ROUTING_KEY = "payment.retry";
    public static final String PAYMENT_DLQ_ROUTING_KEY = "payment.dlq";

    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String PAYMENT_RETRY_QUEUE = "payment.retry.queue";
    public static final String PAYMENT_DLQ = "payment.dlq";

    private RabbitMqConstants() {
    }
}
