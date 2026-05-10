package com.github.mohrezal.worker.config;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class WorkerRabbitConfig {
    private final WorkerProperties workerProperties;

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Notification.Queue.EMAIL)
                .maxPriority(RabbitMQConstants.Notification.MAX_PRIORITY)
                .deadLetterExchange(RabbitMQConstants.DeadLetter.EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.DeadLetter.RoutingKey.EMAIL)
                .lazy()
                .build();
    }

    @Bean
    public Queue verificationReminderDelayQueue() {
        return QueueBuilder.durable(
                        RabbitMQConstants.Notification.Queue.VERIFICATION_REMINDER_DELAY)
                .ttl((int) workerProperties.reminder().ttl().toMillis())
                .deadLetterExchange(RabbitMQConstants.Notification.EXCHANGE)
                .deadLetterRoutingKey(
                        RabbitMQConstants.Notification.RoutingKey.VERIFICATION_REMINDER_CONSUME)
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
    public Binding verificationReminderDelayBinding() {
        return BindingBuilder.bind(verificationReminderDelayQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.Notification.RoutingKey.VERIFICATION_REMINDER_DELAY);
    }

    @Bean
    public Binding verificationReminderConsumeBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.Notification.RoutingKey.VERIFICATION_REMINDER_CONSUME);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMQConstants.DeadLetter.EXCHANGE);
    }

    @Bean
    public Queue deadEmailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DeadLetter.Queue.EMAIL).lazy().build();
    }

    @Bean
    public Binding deadEmailBinding() {
        return BindingBuilder.bind(deadEmailQueue())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.DeadLetter.RoutingKey.EMAIL);
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
        factory.setPrefetchCount(1);
        factory.setConcurrentConsumers(5);
        var retryBuilder =
                RetryInterceptorBuilder.stateless()
                        .maxRetries(2)
                        .backOffOptions(5000L, 2.0, 30000L)
                        .recoverer(new RejectAndDontRequeueRecoverer());

        factory.setAdviceChain(retryBuilder.build());
        return factory;
    }
}
