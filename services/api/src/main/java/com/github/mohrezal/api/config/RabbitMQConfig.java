package com.github.mohrezal.api.config;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.EMAIL_QUEUE)
                .deadLetterExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.DEAD_EMAIL_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue transactionalEmailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.TRANSACTIONAL_EMAIL_QUEUE)
                .deadLetterExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.DEAD_TRANSACTIONAL_EMAIL_ROUTING_KEY)
                .build();
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(RabbitMQConstants.NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding transactionalEmailBinding() {
        return BindingBuilder.bind(transactionalEmailQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.TRANSACTIONAL_EMAIL_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
