package com.github.mohrezal.api.domains.notifications.queries;

import com.github.mohrezal.api.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.api.domains.notifications.mappers.NotificationPreferenceMapper;
import com.github.mohrezal.api.domains.notifications.queries.params.GetNotificationPreferencesQueryParams;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.api.domains.notifications.utils.NotificationUtils;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetNotificationPreferencesQuery
        extends AuthenticatedQuery<
                GetNotificationPreferencesQueryParams, NotificationPreferenceSummary> {
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    @Transactional(readOnly = true)
    @Override
    public NotificationPreferenceSummary execute(GetNotificationPreferencesQueryParams params) {
        validate(params);
        var preference =
                this.notificationPreferenceRepository
                        .findByUserId(user.getId())
                        .orElseGet(NotificationUtils::defaultPreferences);
        return this.notificationPreferenceMapper.toNotificationPreferenceSummary(preference);
    }
}
