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
        return QueueBuilder.durable(RabbitMQConstants.Notification.Queue.EMAIL)
                .maxPriority(10)
                .deadLetterExchange(RabbitMQConstants.DeadLetter.EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.DeadLetter.RoutingKey.EMAIL)
                .lazy()
                .build();
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(RabbitMQConstants.Notification.EXCHANGE);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.Notification.RoutingKey.EMAIL);
    }

    @Bean
    public Binding transactionalEmailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.Notification.RoutingKey.TRANSACTIONAL_EMAIL);
    }

    @Bean
    public Binding verificationReminderConsumeBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.Notification.RoutingKey.VERIFICATION_REMINDER_CONSUME);
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
