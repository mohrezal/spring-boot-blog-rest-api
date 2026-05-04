package com.github.mohrezal.api.domains.notifications.listeners;

import com.github.mohrezal.api.domains.notifications.data.FollowNotificationData;
import com.github.mohrezal.api.domains.notifications.mappers.NotificationMapper;
import com.github.mohrezal.api.domains.notifications.models.Notification;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.domains.notifications.services.sse.NotificationSseService;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.common.constants.RabbitMQConstants;
import com.github.mohrezal.common.constants.Templates;
import com.github.mohrezal.common.worker.events.UserFollowedEvent;
import com.github.mohrezal.common.worker.events.UserRegisteredEvent;
import com.github.mohrezal.common.worker.messaging.TransactionalEmailMessage;
import java.util.Map;
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
public class NotificationEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationSseService sseService;
    private final NotificationMapper notificationMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserFollowedEvent(UserFollowedEvent event) {

        log.debug(
                "UserFollowedEvent: {} followed {}", event.actorHandle(), event.recipientHandle());

        var data = new FollowNotificationData(event.actorId());

        var actor =
                userRepository.findById(event.actorId()).orElseThrow(UserNotFoundException::new);
        var recipient =
                userRepository
                        .findById(event.recipientId())
                        .orElseThrow(UserNotFoundException::new);

        var notification =
                Notification.builder().recipient(recipient).actor(actor).data(data).build();
        notificationRepository.save(notification);

        if (event.preferences().inAppEnabled()) {
            var summary = notificationMapper.toNotificationSummary(notification);
            sseService.push(recipient.getId(), summary);
            log.debug("Pushed in-app notification to user {}", recipient.getId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.debug("UserRegisteredEvent: queuing welcome email for {}", event.email());

        Map<String, Object> variables = Map.of("userName", event.firstName());
        var message =
                new TransactionalEmailMessage(
                        event.email(), "Welcome to Our Blog!", Templates.Email.WELCOME, variables);

        publishTransactionalEmail(message);
    }

    private void publishTransactionalEmail(TransactionalEmailMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.TRANSACTIONAL_EMAIL_ROUTING_KEY,
                message);
        log.debug("Published transactional email to queue for: {}", message.to());
    }

    private void publishToQueue(String routingKey, UUID notificationId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATION_EXCHANGE, routingKey, notificationId);
        log.debug("Published notification {} to queue: {}", notificationId, routingKey);
    }
}
