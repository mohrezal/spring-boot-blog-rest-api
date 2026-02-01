package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params.UpdateNotificationPreferencesCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.UpdateNotificationPreferenceRequest;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers.NotificationPreferenceMapper;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.springbootblogrestapi.shared.abstracts.AuthenticatedCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateNotificationPreferencesCommand
        extends AuthenticatedCommand<
                UpdateNotificationPreferencesCommandParams, NotificationPreferenceSummary> {

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NotificationPreferenceSummary execute(
            UpdateNotificationPreferencesCommandParams params) {
        validate(params);
        UpdateNotificationPreferenceRequest request = params.request();

        NotificationPreference preference =
                notificationPreferenceRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> NotificationPreference.builder().user(user).build());

        preference.setInAppEnabled(request.inAppEnabled());
        preference.setEmailEnabled(request.emailEnabled());

        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        return notificationPreferenceMapper.toNotificationPreferenceSummary(saved);
    }
}
