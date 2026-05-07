package com.github.mohrezal.api.domains.notifications.listeners;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.notifications.data.FollowNotificationData;
import com.github.mohrezal.api.domains.notifications.mappers.NotificationMapper;
import com.github.mohrezal.api.domains.notifications.models.Notification;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.domains.notifications.services.sse.NotificationSseService;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.common.constants.RabbitMQConstants;
import com.github.mohrezal.common.constants.Templates;
import com.github.mohrezal.common.worker.contracts.NotificationPreference;
import com.github.mohrezal.common.worker.events.UserFollowedEvent;
import com.github.mohrezal.common.worker.events.UserRegisteredEvent;
import com.github.mohrezal.common.worker.messaging.TransactionalEmailMessage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock private RabbitTemplate rabbitTemplate;

    @Mock private NotificationRepository notificationRepository;

    @Mock private UserRepository userRepository;

    @Mock private NotificationSseService sseService;

    @Mock private NotificationMapper notificationMapper;

    @InjectMocks private NotificationEventListener listener;

    private User actor;
    private User recipient;
    private NotificationPreference preferences;

    @BeforeEach
    void setUp() {
        actor =
                aUser().withId(UUID.randomUUID())
                        .withEmail("actor@example.com")
                        .withHandle("actor")
                        .build();

        recipient =
                aUser().withId(UUID.randomUUID())
                        .withEmail("recipient@example.com")
                        .withHandle("recipient")
                        .build();

        preferences = new NotificationPreference(true, true);
    }

    @Test
    void handleUserFollowedEvent_whenInAppEnabled_shouldPushSse() {
        UserFollowedEvent event =
                new UserFollowedEvent(
                        actor.getId(), actor.getHandle(),
                        recipient.getId(), recipient.getHandle(),
                        recipient.getEmail(), preferences);

        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(userRepository.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        listener.handleUserFollowedEvent(event);

        verify(sseService).push(eq(recipient.getId()), any());
    }

    @Test
    void handleUserFollowedEvent_whenInAppDisabled_shouldNotPushSse() {
        NotificationPreference disabled = new NotificationPreference(false, true);
        UserFollowedEvent event =
                new UserFollowedEvent(
                        actor.getId(), actor.getHandle(),
                        recipient.getId(), recipient.getHandle(),
                        recipient.getEmail(), disabled);

        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(userRepository.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        listener.handleUserFollowedEvent(event);

        verify(sseService, never()).push(any(), any());
    }

    @Test
    void handleUserFollowedEvent_whenEmailDisabled_shouldNotPublishToEmailQueue() {
        NotificationPreference disabled = new NotificationPreference(true, false);
        UserFollowedEvent event =
                new UserFollowedEvent(
                        actor.getId(), actor.getHandle(),
                        recipient.getId(), recipient.getHandle(),
                        recipient.getEmail(), disabled);

        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(userRepository.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        listener.handleUserFollowedEvent(event);

        verify(rabbitTemplate, never())
                .convertAndSend(
                        eq(RabbitMQConstants.Notification.EXCHANGE),
                        eq(RabbitMQConstants.Notification.RoutingKey.EMAIL),
                        any(Object.class));
    }

    @Test
    void handleUserFollowedEvent_whenCalled_shouldPersistNotification() {
        UserFollowedEvent event =
                new UserFollowedEvent(
                        actor.getId(), actor.getHandle(),
                        recipient.getId(), recipient.getHandle(),
                        recipient.getEmail(), preferences);

        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(userRepository.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        listener.handleUserFollowedEvent(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(recipient, saved.getRecipient());
        assertEquals(actor, saved.getActor());
        assertNotNull(saved.getData());
        assertEquals(actor.getId(), ((FollowNotificationData) saved.getData()).actorId());
    }

    @Test
    void handleUserRegisteredEvent_whenCalled_shouldPublishWelcomeEmail() {
        UserRegisteredEvent event =
                new UserRegisteredEvent(
                        UUID.randomUUID(),
                        "John",
                        "Doe",
                        "newuser@example.com",
                        "random-token",
                        "http://localhost:3000");

        listener.handleUserRegisteredEvent(event);

        var captor = ArgumentCaptor.forClass(TransactionalEmailMessage.class);
        verify(rabbitTemplate)
                .convertAndSend(
                        eq(RabbitMQConstants.Notification.EXCHANGE),
                        eq(RabbitMQConstants.Notification.RoutingKey.TRANSACTIONAL_EMAIL),
                        captor.capture());

        var message = captor.getValue();
        assertEquals("newuser@example.com", message.to());
        assertEquals("Welcome to Our Blog!", message.subject());
        assertEquals(Templates.Email.WELCOME, message.template());
        assertEquals("John", message.variables().get("userName"));
    }
}
