package org.erick.paymentconsumer.config;

import org.erick.shared.util.RabbitMqConstants;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
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
                .deadLetterExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitMqConstants.PAYMENT_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue paymentDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMqConstants.PAYMENT_DLQ).build();
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
    MessageRecoverer messageRecoverer() {
        return new RejectAndDontRequeueRecoverer();
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter,
            MessageRecoverer messageRecoverer
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1_000, 2.0, 5_000)
                .recoverer(messageRecoverer)
                .build());
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }
}
