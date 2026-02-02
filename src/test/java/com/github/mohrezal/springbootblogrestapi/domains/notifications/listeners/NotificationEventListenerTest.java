package com.github.mohrezal.springbootblogrestapi.domains.notifications.listeners;

import static com.github.mohrezal.springbootblogrestapi.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.config.RabbitMQConfig;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.data.FollowNotificationData;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.events.UserFollowedEvent;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.events.UserRegisteredEvent;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.messages.TransactionalEmailMessage;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.utils.Templates;
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

    @Mock private NotificationPreferenceRepository preferenceRepository;

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks private NotificationEventListener listener;

    private User actor;
    private User recipient;

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
    }

    @Test
    void handleUserFollowedEvent_whenInAppEnabled_shouldPublishToInAppQueue() {
        NotificationPreference preferences =
                NotificationPreference.builder().inAppEnabled(true).emailEnabled(true).build();

        when(preferenceRepository.findByUserId(recipient.getId()))
                .thenReturn(Optional.of(preferences));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        UserFollowedEvent event = new UserFollowedEvent(actor, recipient);

        listener.handleUserFollowedEvent(event);

        verify(rabbitTemplate)
                .convertAndSend(
                        eq(RabbitMQConfig.EXCHANGE),
                        eq(RabbitMQConfig.INAPP_ROUTING_KEY),
                        any(UUID.class));
    }

    @Test
    void handleUserFollowedEvent_whenInAppDisabled_shouldNotPublishToQueue() {
        NotificationPreference preferences =
                NotificationPreference.builder().inAppEnabled(false).emailEnabled(true).build();

        when(preferenceRepository.findByUserId(recipient.getId()))
                .thenReturn(Optional.of(preferences));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        UserFollowedEvent event = new UserFollowedEvent(actor, recipient);

        listener.handleUserFollowedEvent(event);

        verify(rabbitTemplate, never())
                .convertAndSend(
                        eq(RabbitMQConfig.EXCHANGE),
                        eq(RabbitMQConfig.INAPP_ROUTING_KEY),
                        any(UUID.class));
    }

    @Test
    void handleUserFollowedEvent_whenNoPreferences_shouldUseDefaults() {
        when(preferenceRepository.findByUserId(recipient.getId())).thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        UserFollowedEvent event = new UserFollowedEvent(actor, recipient);

        listener.handleUserFollowedEvent(event);

        verify(rabbitTemplate)
                .convertAndSend(
                        eq(RabbitMQConfig.EXCHANGE),
                        eq(RabbitMQConfig.INAPP_ROUTING_KEY),
                        any(UUID.class));
    }

    @Test
    void handleUserFollowedEvent_whenCalled_shouldPersistNotification() {
        NotificationPreference preferences =
                NotificationPreference.builder().inAppEnabled(true).emailEnabled(true).build();

        when(preferenceRepository.findByUserId(recipient.getId()))
                .thenReturn(Optional.of(preferences));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(
                        invocation -> {
                            Notification n = invocation.getArgument(0);
                            n.setId(UUID.randomUUID());
                            return n;
                        });

        UserFollowedEvent event = new UserFollowedEvent(actor, recipient);

        listener.handleUserFollowedEvent(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification savedNotification = captor.getValue();
        assertEquals(recipient, savedNotification.getRecipient());
        assertEquals(actor, savedNotification.getActor());
        assertNotNull(savedNotification.getData());
        assertEquals(
                actor.getId(), ((FollowNotificationData) savedNotification.getData()).actorId());
    }

    @Test
    void handleUserRegisteredEvent_whenCalled_shouldPublishWelcomeEmail() {
        User newUser =
                aUser().withId(UUID.randomUUID())
                        .withEmail("newuser@example.com")
                        .withFirstName("John")
                        .build();

        UserRegisteredEvent event = new UserRegisteredEvent(newUser);

        listener.handleUserRegisteredEvent(event);

        ArgumentCaptor<TransactionalEmailMessage> captor =
                ArgumentCaptor.forClass(TransactionalEmailMessage.class);
        verify(rabbitTemplate)
                .convertAndSend(
                        eq(RabbitMQConfig.EXCHANGE),
                        eq(RabbitMQConfig.TRANSACTIONAL_EMAIL_ROUTING_KEY),
                        captor.capture());

        TransactionalEmailMessage message = captor.getValue();
        assertEquals(newUser.getEmail(), message.to());
        assertEquals("Welcome to Our Blog!", message.subject());
        assertEquals(Templates.Email.WELCOME, message.template());
        assertEquals(newUser.getFirstName(), message.variables().get("userName"));
    }
}
