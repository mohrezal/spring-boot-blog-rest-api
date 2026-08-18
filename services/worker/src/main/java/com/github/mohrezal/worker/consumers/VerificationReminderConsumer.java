package com.github.mohrezal.worker.consumers;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import com.github.mohrezal.common.constants.Templates;
import com.github.mohrezal.common.redis.RedisCacheService;
import com.github.mohrezal.common.redis.constants.RedisKeyFactory;
import com.github.mohrezal.common.worker.messaging.VerificationReminderMessage;
import com.github.mohrezal.worker.services.email.EmailProvider;
import com.rabbitmq.client.Channel;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class VerificationReminderConsumer {
    private final EmailProvider emailProvider;
    private final RedisCacheService redisCacheService;

    @RabbitListener(
            queues = RabbitMQConstants.Notification.Queue.VERIFICATION_REMINDER,
            containerFactory = "manualAckContainerFactory")
    public void consume(
            VerificationReminderMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag,
            @Header(name = RabbitMQConstants.Header.MESSAGE_ID, required = false)
                    String messageId) {
        try {
            if (messageId != null
                    && redisCacheService
                            .get(RedisKeyFactory.Notification.delivered(messageId), String.class)
                            .isPresent()) {
                log.info("Duplicate transactional email {} – skipping", messageId);
                channel.basicAck(tag, false);
                return;
            }
            Optional<String> token =
                    redisCacheService.get(
                            RedisKeyFactory.Verification.token(message.token()), String.class);
            if (token.isEmpty()) {
                log.info("Token not found for {} – skipping reminder", message.to());
                channel.basicAck(tag, false);
                return;
            }
            emailProvider.send(
                    message.to(),
                    "Please verify your account",
                    Templates.Email.VERIFICATION_REMINDER,
                    message.variables());
            if (messageId != null) {
                redisCacheService.set(
                        RedisKeyFactory.Notification.delivered(messageId),
                        "1",
                        Duration.ofSeconds(RedisKeyFactory.Notification.TTL_SECONDS));
            }

            channel.basicAck(tag, false);

            log.info("Verification reminder sent to {}", message.to());
        } catch (Exception e) {
            log.error("Failed to send reminder email to {}", message.to(), e);
            throw new RuntimeException(e);
        }
    }
}
