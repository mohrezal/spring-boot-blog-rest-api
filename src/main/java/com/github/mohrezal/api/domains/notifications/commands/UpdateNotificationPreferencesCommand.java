package com.github.mohrezal.api.domains.notifications.commands;

import com.github.mohrezal.api.domains.notifications.commands.params.UpdateNotificationPreferencesCommandParams;
import com.github.mohrezal.api.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.api.domains.notifications.mappers.NotificationPreferenceMapper;
import com.github.mohrezal.api.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateNotificationPreferencesCommand
        extends AuthenticatedCommand<
                UpdateNotificationPreferencesCommandParams, NotificationPreferenceSummary> {

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NotificationPreferenceSummary execute(
            UpdateNotificationPreferencesCommandParams params) {
        var currentUser = getCurrentUser(params);

        var request = params.request();

        var preference =
                notificationPreferenceRepository
                        .findByUserId(currentUser.getId())
                        .orElseGet(
                                () -> NotificationPreference.builder().user(currentUser).build());

        preference.setInAppEnabled(request.inAppEnabled());
        preference.setEmailEnabled(request.emailEnabled());

        var saved = notificationPreferenceRepository.save(preference);
        log.info("Update notification preferences command successful.");
        return notificationPreferenceMapper.toNotificationPreferenceSummary(saved);
    }
}
