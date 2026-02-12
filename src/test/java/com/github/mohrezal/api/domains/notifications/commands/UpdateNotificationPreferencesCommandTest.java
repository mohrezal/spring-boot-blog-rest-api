package com.github.mohrezal.api.domains.notifications.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.notifications.commands.params.UpdateNotificationPreferencesCommandParams;
import com.github.mohrezal.api.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.api.domains.notifications.dtos.UpdateNotificationPreferenceRequest;
import com.github.mohrezal.api.domains.notifications.mappers.NotificationPreferenceMapper;
import com.github.mohrezal.api.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.api.domains.users.models.User;
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
class UpdateNotificationPreferencesCommandTest {

    @Mock private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock private NotificationPreferenceMapper notificationPreferenceMapper;

    @InjectMocks private UpdateNotificationPreferencesCommand command;

    private User user;

    @BeforeEach
    void setUp() {
        user = aUser().withId(UUID.randomUUID()).build();
    }

    @Test
    void execute_whenPreferenceExists_shouldUpdateExistingPreference() {
        NotificationPreference existingPreference =
                NotificationPreference.builder()
                        .id(UUID.randomUUID())
                        .user(user)
                        .inAppEnabled(true)
                        .emailEnabled(true)
                        .build();

        UpdateNotificationPreferenceRequest request =
                new UpdateNotificationPreferenceRequest(false, false);

        var params = new UpdateNotificationPreferencesCommandParams(user, request);

        when(notificationPreferenceRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(existingPreference));
        when(notificationPreferenceRepository.save(any(NotificationPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        command.execute(params);

        ArgumentCaptor<NotificationPreference> captor =
                ArgumentCaptor.forClass(NotificationPreference.class);
        verify(notificationPreferenceRepository).save(captor.capture());

        NotificationPreference saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(existingPreference.getId());
        assertThat(saved.getInAppEnabled()).isFalse();
        assertThat(saved.getEmailEnabled()).isFalse();
    }

    @Test
    void execute_whenPreferenceNotExists_shouldCreateNewPreference() {
        UpdateNotificationPreferenceRequest request =
                new UpdateNotificationPreferenceRequest(true, false);

        var params = new UpdateNotificationPreferencesCommandParams(user, request);

        when(notificationPreferenceRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());
        when(notificationPreferenceRepository.save(any(NotificationPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        command.execute(params);

        ArgumentCaptor<NotificationPreference> captor =
                ArgumentCaptor.forClass(NotificationPreference.class);
        verify(notificationPreferenceRepository).save(captor.capture());

        NotificationPreference saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getInAppEnabled()).isTrue();
        assertThat(saved.getEmailEnabled()).isFalse();
    }

    @Test
    void execute_shouldReturnMappedSummary() {
        NotificationPreference existingPreference =
                NotificationPreference.builder()
                        .id(UUID.randomUUID())
                        .user(user)
                        .inAppEnabled(true)
                        .emailEnabled(true)
                        .build();

        UpdateNotificationPreferenceRequest request =
                new UpdateNotificationPreferenceRequest(true, true);

        var params = new UpdateNotificationPreferencesCommandParams(user, request);

        NotificationPreferenceSummary expectedSummary =
                new NotificationPreferenceSummary(true, true);

        when(notificationPreferenceRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(existingPreference));
        when(notificationPreferenceRepository.save(any(NotificationPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationPreferenceMapper.toNotificationPreferenceSummary(
                        any(NotificationPreference.class)))
                .thenReturn(expectedSummary);

        NotificationPreferenceSummary result = command.execute(params);

        assertThat(result).isEqualTo(expectedSummary);
        verify(notificationPreferenceMapper)
                .toNotificationPreferenceSummary(any(NotificationPreference.class));
    }
}
