package org.erick.paymentconsumer.config;

import org.erick.paymentconsumer.messaging.RabbitMqConstants;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitPaymentConfig {

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
                .build();
    }

    @Bean
    Queue paymentDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMqConstants.PAYMENT_DLQ).build();
    }

    @Bean
    Queue paymentRetryQueue() {
        return QueueBuilder.durable(RabbitMqConstants.PAYMENT_RETRY_QUEUE)
                .ttl(10000)
                .deadLetterExchange(RabbitMqConstants.ORDER_EVENTS_EXCHANGE)
                .deadLetterRoutingKey(RabbitMqConstants.ORDER_CREATED_ROUTING_KEY)
                .build();
    }

    @Bean
    Binding paymentBinding(
            @Qualifier("paymentQueue") Queue paymentQueue,
            @Qualifier("orderEventsExchange") DirectExchange orderEventsExchange
    ) {
        return BindingBuilder.bind(paymentQueue)
                .to(orderEventsExchange)
                .with(RabbitMqConstants.ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    Binding paymentRetryBinding(
            @Qualifier("paymentRetryQueue") Queue paymentRetryQueue,
            @Qualifier("orderEventsExchange") DirectExchange orderEventsExchange
    ) {
        return BindingBuilder.bind(paymentRetryQueue)
                .to(orderEventsExchange)
                .with(RabbitMqConstants.PAYMENT_RETRY_ROUTING_KEY);
    }

    @Bean
    Binding paymentDeadLetterBinding(
            @Qualifier("paymentDeadLetterQueue") Queue paymentDeadLetterQueue,
            @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange
    ) {
        return BindingBuilder.bind(paymentDeadLetterQueue)
                .to(deadLetterExchange)
                .with(RabbitMqConstants.PAYMENT_DLQ_ROUTING_KEY);
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);

        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
