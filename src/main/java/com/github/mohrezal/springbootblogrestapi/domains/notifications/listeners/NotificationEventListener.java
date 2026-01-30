package com.github.mohrezal.springbootblogrestapi.domains.notifications.listeners;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.config.RabbitMQConfig;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.data.FollowNotificationData;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.events.UserFollowedEvent;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class NotificationEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserFollowedEvent(UserFollowedEvent event) {
        User recipient = event.recipient();
        log.debug("UserFollowedEvent: {} followed {}", event.actorHandle(), recipient.getHandle());

        NotificationPreference preferences =
                preferenceRepository
                        .findByUserId(recipient.getId())
                        .orElseGet(this::defaultPreferences);

        FollowNotificationData data =
                new FollowNotificationData(event.actorId(), event.actorName(), event.actorHandle());

        Notification notification = Notification.builder().recipient(recipient).data(data).build();
        notificationRepository.save(notification);

        if (preferences.getInAppEnabled()) {
            publishToQueue(RabbitMQConfig.INAPP_ROUTING_KEY, notification.getId());
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

    private NotificationPreference defaultPreferences() {
        return NotificationPreference.builder().inAppEnabled(true).emailEnabled(true).build();
    }
}
