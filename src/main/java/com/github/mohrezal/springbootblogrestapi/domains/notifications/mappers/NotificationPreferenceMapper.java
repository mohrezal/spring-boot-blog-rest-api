package com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationPreferenceMapper {
    NotificationPreferenceSummary toNotificationPreferenceSummary(
            NotificationPreference notificationPreference);
}
