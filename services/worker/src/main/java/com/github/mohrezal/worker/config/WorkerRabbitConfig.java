package com.github.mohrezal.worker.config;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class WorkerRabbitConfig {
    @Bean
    public Queue emailQueue() {
        return new Queue(RabbitMQConstants.EMAIL_QUEUE, true);
    }

    @Bean
    public Queue transactionalEmailQueue() {
        return new Queue(RabbitMQConstants.TRANSACTIONAL_EMAIL_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(exchange())
                .with(RabbitMQConstants.EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding transactionalEmailBinding() {
        return BindingBuilder.bind(transactionalEmailQueue())
                .to(exchange())
                .with(RabbitMQConstants.TRANSACTIONAL_EMAIL_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
