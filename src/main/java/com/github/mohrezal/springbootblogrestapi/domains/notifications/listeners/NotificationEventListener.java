package com.github.mohrezal.springbootblogrestapi.domains.notifications.listeners;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.config.RabbitMQConfig;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.data.FollowNotificationData;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.events.UserFollowedEvent;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.events.UserRegisteredEvent;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.messages.TransactionalEmailMessage;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.utils.NotificationUtils;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.utils.Templates;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserFollowedEvent(UserFollowedEvent event) {
        User actor = event.actor();
        User recipient = event.recipient();
        log.debug("UserFollowedEvent: {} followed {}", actor.getHandle(), recipient.getHandle());

        NotificationPreference preferences =
                preferenceRepository
                        .findByUserId(recipient.getId())
                        .orElseGet(NotificationUtils::defaultPreferences);

        FollowNotificationData data = new FollowNotificationData(actor.getId());

        Notification notification =
                Notification.builder().recipient(recipient).actor(actor).data(data).build();
        notificationRepository.save(notification);

        if (preferences.getInAppEnabled()) {
            publishToQueue(RabbitMQConfig.INAPP_ROUTING_KEY, notification.getId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.user();
        log.debug("UserRegisteredEvent: queuing welcome email for {}", user.getEmail());

        Map<String, Object> variables = Map.of("userName", user.getFirstName());
        TransactionalEmailMessage message =
                new TransactionalEmailMessage(
                        user.getEmail(),
                        "Welcome to Our Blog!",
                        Templates.Email.WELCOME,
                        variables);

        publishTransactionalEmail(message);
    }

    private void publishTransactionalEmail(TransactionalEmailMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.TRANSACTIONAL_EMAIL_ROUTING_KEY,
                    message);
            log.debug("Published transactional email to queue for: {}", message.to());
        } catch (Exception e) {
            log.error("Failed to publish transactional email to queue: {}", e.getMessage());
        }
    }

    private void publishToQueue(String routingKey, UUID notificationId) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, notificationId);
            log.debug("Published notification {} to queue: {}", notificationId, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish notification to queue {}: {}", routingKey, e.getMessage());
        }
    }
}
