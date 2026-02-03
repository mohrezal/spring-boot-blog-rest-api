package com.github.mohrezal.api.domains.notifications.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.github.mohrezal.api.domains.notifications.commands.params.MarkAllNotificationsReadCommandParams;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.domains.users.models.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarkAllNotificationsReadCommandTest {

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks private MarkAllNotificationsReadCommand command;

    private User user;
    private MarkAllNotificationsReadCommandParams params;

    @BeforeEach
    void setUp() {
        user = aUser().withId(UUID.randomUUID()).withEmail("recipient@example.com").build();
        params = MarkAllNotificationsReadCommandParams.builder().userDetails(user).build();
    }

    @Test
    void execute_whenValidUser_shouldCallRepositoryWithUserId() {

        command.execute(params);

        verify(notificationRepository)
                .markAllAsReadByRecipientId(eq(user.getId()), any(OffsetDateTime.class));
    }

    @Test
    void execute_whenValidUser_shouldPassCurrentTimestamp() {
        OffsetDateTime before = OffsetDateTime.now();

        command.execute(params);

        ArgumentCaptor<OffsetDateTime> captor = ArgumentCaptor.forClass(OffsetDateTime.class);
        verify(notificationRepository)
                .markAllAsReadByRecipientId(eq(user.getId()), captor.capture());

        OffsetDateTime captured = captor.getValue();
        OffsetDateTime after = OffsetDateTime.now();

        assertThat(captured).isBetween(before, after);
    }
}
