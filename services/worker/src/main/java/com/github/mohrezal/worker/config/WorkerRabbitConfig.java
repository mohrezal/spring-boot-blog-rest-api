package com.github.mohrezal.worker.config;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class WorkerRabbitConfig {
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
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue deadEmailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DEAD_EMAIL_QUEUE).build();
    }

    @Bean
    public Queue deadTransactionalEmailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DEAD_TRANSACTIONAL_EMAIL_QUEUE).build();
    }

    @Bean
    public Binding deadEmailBinding() {
        return BindingBuilder.bind(deadEmailQueue())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.DEAD_EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding deadTransactionalEmailBinding() {
        return BindingBuilder.bind(deadTransactionalEmailQueue())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.DEAD_TRANSACTIONAL_EMAIL_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory manualAckContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        var retryBuilder =
                RetryInterceptorBuilder.stateless()
                        .maxRetries(2)
                        .backOffOptions(5000L, 2.0, 30000L)
                        .recoverer(new RejectAndDontRequeueRecoverer());

        factory.setAdviceChain(retryBuilder.build());

        return factory;
    }
}
