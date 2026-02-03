package com.github.mohrezal.api.domains.notifications.consumers;

import com.github.mohrezal.api.domains.notifications.config.RabbitMQConfig;
import com.github.mohrezal.api.domains.notifications.data.FollowNotificationData;
import com.github.mohrezal.api.domains.notifications.data.NotificationData;
import com.github.mohrezal.api.domains.notifications.models.Notification;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.domains.notifications.services.email.EmailProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmailProvider emailProvider;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consume(UUID notificationId) {
        log.debug("Received email notification: {}", notificationId);

        notificationRepository
                .findById(notificationId)
                .ifPresentOrElse(
                        this::sendEmail,
                        () -> log.warn("Notification not found: {}", notificationId));
    }

    private void sendEmail(Notification notification) {
        NotificationData data = notification.getData();

        switch (data) {
            case FollowNotificationData _ -> log.debug("Follow notifications do not support email");
        }
    }
}
