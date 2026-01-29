package com.github.mohrezal.springbootblogrestapi.domains.notifications.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfigImpl implements RabbitMQConfig {
    public static final String INAPP_QUEUE = "notification.inapp.queue";
    public static final String EMAIL_QUEUE = "notification.email.queue";

    public static final String EXCHANGE = "notification.exchange";

    @Bean
    @Override
    public Queue inAppQueue() {
        return new Queue(INAPP_QUEUE, true);
    }

    @Bean
    @Override
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    @Override
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    @Override
    public Binding inAppBinding() {
        return BindingBuilder.bind(inAppQueue()).to(exchange()).with(INAPP_QUEUE);
    }

    @Bean
    @Override
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(exchange()).with(EMAIL_QUEUE);
    }
}
