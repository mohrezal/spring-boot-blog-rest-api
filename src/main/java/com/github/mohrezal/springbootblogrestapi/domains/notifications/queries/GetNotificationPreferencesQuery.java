package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers.NotificationPreferenceMapper;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.GetNotificationPreferencesQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.utils.NotificationUtils;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNotificationPreferencesQuery
        implements Query<GetNotificationPreferencesQueryParams, NotificationPreferenceSummary> {
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    @Transactional(readOnly = true)
    @Override
    public NotificationPreferenceSummary execute(GetNotificationPreferencesQueryParams params) {
        User user = (User) params.getUserDetails();
        NotificationPreference preference =
                this.notificationPreferenceRepository
                        .findByUserId(user.getId())
                        .orElseGet(NotificationUtils::defaultPreferences);
        return this.notificationPreferenceMapper.toNotificationPreferenceSummary(preference);
    }
}
