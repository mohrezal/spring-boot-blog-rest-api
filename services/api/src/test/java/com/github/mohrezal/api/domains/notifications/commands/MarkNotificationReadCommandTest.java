package com.github.mohrezal.api.domains.notifications.commands;

import static com.github.mohrezal.api.support.builders.NotificationBuilder.aNotification;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.notifications.commands.params.MarkNotificationReadCommandParams;
import com.github.mohrezal.api.domains.notifications.exceptions.types.NotificationNotFoundException;
import com.github.mohrezal.api.domains.notifications.models.Notification;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarkNotificationReadCommandTest {

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks private MarkNotificationReadCommand command;

    private User recipient;
    private User otherUser;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        recipient = aUser().withId(UUID.randomUUID()).withEmail("recipient@example.com").build();

        otherUser = aUser().withId(UUID.randomUUID()).withEmail("other@example.com").build();

        notificationId = UUID.randomUUID();
    }

    @Test
    void execute_whenValidNotification_shouldMarkAsRead() {
        Notification notification =
                aNotification()
                        .withId(notificationId)
                        .withRecipient(recipient)
                        .withFollowData(UUID.randomUUID())
                        .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var params = new MarkNotificationReadCommandParams(notificationId, recipient);
        command.execute(params);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification savedNotification = captor.getValue();
        assertTrue(savedNotification.getIsRead());
        assertNotNull(savedNotification.getReadAt());
    }

    @Test
    void execute_whenNotificationNotFound_shouldThrowNotFoundException() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        var params = new MarkNotificationReadCommandParams(notificationId, recipient);
        assertThrows(NotificationNotFoundException.class, () -> command.execute(params));
    }

    @Test
    void execute_whenUserNotRecipient_shouldThrowAccessDenied() {
        Notification notification =
                aNotification()
                        .withId(notificationId)
                        .withRecipient(recipient)
                        .withFollowData(UUID.randomUUID())
                        .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        var params = new MarkNotificationReadCommandParams(notificationId, otherUser);

        assertThrows(AccessDeniedException.class, () -> command.execute(params));
    }

    @Test
    void execute_whenAlreadyRead_shouldRemainIdempotent() {
        OffsetDateTime originalReadAt = OffsetDateTime.now().minusHours(1);
        Notification notification =
                aNotification()
                        .withId(notificationId)
                        .withRecipient(recipient)
                        .withFollowData(UUID.randomUUID())
                        .withIsRead(true)
                        .withReadAt(originalReadAt)
                        .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        var params = new MarkNotificationReadCommandParams(notificationId, recipient);

        command.execute(params);

        verify(notificationRepository, never()).save(any());
        assertTrue(notification.getIsRead());
        assertEquals(originalReadAt, notification.getReadAt());
    }
}
