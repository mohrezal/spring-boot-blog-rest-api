package com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationSummary toNotificationSummary(Notification notification);
}
