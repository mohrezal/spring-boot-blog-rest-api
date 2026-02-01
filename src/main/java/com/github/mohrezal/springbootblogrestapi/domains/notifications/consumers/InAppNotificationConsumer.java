package com.github.mohrezal.springbootblogrestapi.domains.notifications.consumers;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.config.RabbitMQConfig;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers.NotificationMapper;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.services.sse.NotificationSseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InAppNotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationSseService sseService;

    @RabbitListener(queues = RabbitMQConfig.INAPP_QUEUE)
    public void consume(UUID notificationId) {
        log.debug("Received in-app notification: {}", notificationId);

        notificationRepository
                .findWithActorById(notificationId)
                .ifPresentOrElse(
                        this::pushNotification,
                        () -> log.warn("Notification not found: {}", notificationId));
    }

    private void pushNotification(Notification notification) {
        UUID recipientId = notification.getRecipient().getId();
        NotificationSummary summary = notificationMapper.toNotificationSummary(notification);
        sseService.push(recipientId, summary);
        log.debug("Pushed notification {} to user {}", notification.getId(), recipientId);
    }
}
