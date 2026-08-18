package com.github.mohrezal.worker.consumers;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import com.github.mohrezal.common.redis.RedisCacheService;
import com.github.mohrezal.common.redis.constants.RedisKeyFactory;
import com.github.mohrezal.common.worker.messaging.TransactionalEmailMessage;
import com.github.mohrezal.worker.services.email.EmailProvider;
import com.rabbitmq.client.Channel;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalEmailConsumer {

    private final EmailProvider emailProvider;
    private final RedisCacheService redisCacheService;

    @RabbitListener(
            queues = RabbitMQConstants.Notification.Queue.EMAIL,
            containerFactory = "manualAckContainerFactory")
    public void consume(
            TransactionalEmailMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag,
            @Header(name = RabbitMQConstants.Header.MESSAGE_ID, required = false)
                    String messageId) {
        deliver(message, channel, tag, messageId);
    }

    @RabbitListener(
            queues = RabbitMQConstants.Notification.Queue.EMAIL_LOW,
            containerFactory = "lowPriorityEmailContainerFactory")
    public void consumeLowPriority(
            TransactionalEmailMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag,
            @Header(name = RabbitMQConstants.Header.MESSAGE_ID, required = false)
                    String messageId) {
        deliver(message, channel, tag, messageId);
    }

    private void deliver(
            TransactionalEmailMessage message, Channel channel, long tag, String messageId) {
        try {
            if (messageId != null
                    && redisCacheService
                            .get(RedisKeyFactory.Notification.delivered(messageId), String.class)
                            .isPresent()) {
                log.info("Duplicate transactional email {} – skipping", messageId);
                channel.basicAck(tag, false);
                return;
            }
            log.debug("Received transactional email for: {}", message.to());

            emailProvider.send(
                    message.to(), message.subject(),
                    message.template(), message.variables());

            if (messageId != null) {
                redisCacheService.set(
                        RedisKeyFactory.Notification.delivered(messageId),
                        "1",
                        Duration.ofSeconds(RedisKeyFactory.Notification.TTL_SECONDS));
            }

            channel.basicAck(tag, false);
            log.info("Transactional email sent to {}", message.to());
        } catch (Exception e) {
            log.error("Failed to send email to {}", message.to(), e);

            throw new RuntimeException(e);
        }
    }
}
