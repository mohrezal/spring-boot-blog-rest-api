package com.github.mohrezal.api.domains.notifications.queries;

import com.github.mohrezal.api.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.api.domains.notifications.mappers.NotificationPreferenceMapper;
import com.github.mohrezal.api.domains.notifications.queries.params.GetNotificationPreferencesQueryParams;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.api.domains.notifications.utils.NotificationUtils;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetNotificationPreferencesQuery
        extends AuthenticatedQuery<
                GetNotificationPreferencesQueryParams, NotificationPreferenceSummary> {
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    @Transactional(readOnly = true)
    @Override
    public NotificationPreferenceSummary execute(GetNotificationPreferencesQueryParams params) {
        var currentUser = getCurrentUser(params);

        var preference =
                this.notificationPreferenceRepository
                        .findByUserId(currentUser.getId())
                        .orElseGet(NotificationUtils::defaultPreferences);
        var response =
                this.notificationPreferenceMapper.toNotificationPreferenceSummary(preference);
        log.info("Get notification preferences query successful.");
        return response;
    }
}
