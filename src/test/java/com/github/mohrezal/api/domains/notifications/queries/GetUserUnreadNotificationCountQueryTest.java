package com.github.mohrezal.api.domains.notifications.queries;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.notifications.queries.params.GetUserUnreadNotificationCountQueryParams;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.domains.users.models.User;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserUnreadNotificationCountQueryTest {

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks private GetUserUnreadNotificationCountQuery query;

    private User user;

    @BeforeEach
    void setUp() {
        user = aUser().withId(UUID.randomUUID()).build();
    }

    @Test
    void execute_whenValidUser_shouldReturnUnreadCount() {
        GetUserUnreadNotificationCountQueryParams params =
                GetUserUnreadNotificationCountQueryParams.builder().userDetails(user).build();

        when(notificationRepository.countByRecipientAndIsRead(user, false)).thenReturn(5);

        Integer result = query.execute(params);

        assertThat(result).isEqualTo(5);
        verify(notificationRepository).countByRecipientAndIsRead(user, false);
    }

    @Test
    void execute_whenNoUnreadNotifications_shouldReturnZero() {
        GetUserUnreadNotificationCountQueryParams params =
                GetUserUnreadNotificationCountQueryParams.builder().userDetails(user).build();

        when(notificationRepository.countByRecipientAndIsRead(user, false)).thenReturn(0);

        Integer result = query.execute(params);

        assertThat(result).isZero();
    }
}
