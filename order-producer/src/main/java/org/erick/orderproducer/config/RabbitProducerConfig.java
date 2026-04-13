package org.erick.orderproducer.config;

import org.erick.orderproducer.messaging.RabbitMqConstants;
import org.springframework.amqp.core.DirectExchange;
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

}
