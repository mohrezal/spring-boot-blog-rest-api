package com.github.mohrezal.springbootblogrestapi.domains.notifications.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String INAPP_QUEUE = "notification.inapp.queue";
    public static final String EMAIL_QUEUE = "notification.email.queue";

    public static final String INAPP_ROUTING_KEY = "notification.inapp";
    public static final String EMAIL_ROUTING_KEY = "notification.email";

    public static final String EXCHANGE = "notification.exchange";

    @Bean
    public Queue inAppQueue() {
        return new Queue(INAPP_QUEUE, true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding inAppBinding() {
        return BindingBuilder.bind(inAppQueue()).to(exchange()).with(INAPP_ROUTING_KEY);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(exchange()).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
