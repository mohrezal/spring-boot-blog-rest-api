package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params.UpdateNotificationPreferencesCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.UpdateNotificationPreferenceRequest;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers.NotificationPreferenceMapper;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateNotificationPreferencesCommand
        implements Command<
                UpdateNotificationPreferencesCommandParams, NotificationPreferenceSummary> {

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NotificationPreferenceSummary execute(
            UpdateNotificationPreferencesCommandParams params) {
        User user = (User) params.getUserDetails();
        UpdateNotificationPreferenceRequest request = params.getRequest();

        NotificationPreference preference =
                notificationPreferenceRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> NotificationPreference.builder().user(user).build());

        preference.setInAppEnabled(request.isInAppEnabled());
        preference.setEmailEnabled(request.isEmailEnabled());

        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        return notificationPreferenceMapper.toNotificationPreferenceSummary(saved);
    }
}
