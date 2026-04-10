package org.erick.orderproducer.config;

import org.erick.shared.util.RabbitMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitProducerConfig {

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DirectExchange orderEventsExchange() {
        return new DirectExchange(RabbitMqConstants.ORDER_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Queue paymentQueue() {
        return QueueBuilder.durable(RabbitMqConstants.PAYMENT_QUEUE)
                .deadLetterExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitMqConstants.PAYMENT_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue paymentDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMqConstants.PAYMENT_DLQ).build();
    }

    @Bean
    Queue notificationQueue() {
        return QueueBuilder.durable(RabbitMqConstants.NOTIFICATION_QUEUE).build();
    }

    @Bean
    Binding paymentBinding(Queue paymentQueue, DirectExchange orderEventsExchange) {
        return BindingBuilder.bind(paymentQueue)
                .to(orderEventsExchange)
                .with(RabbitMqConstants.ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    Binding paymentDeadLetterBinding(Queue paymentDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(paymentDeadLetterQueue)
                .to(deadLetterExchange)
                .with(RabbitMqConstants.PAYMENT_DLQ_ROUTING_KEY);
    }

    @Bean
    Binding notificationBinding(Queue notificationQueue, DirectExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(orderEventsExchange)
                .with(RabbitMqConstants.PAYMENT_PROCESSED_ROUTING_KEY);
    }
}
