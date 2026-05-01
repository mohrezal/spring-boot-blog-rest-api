package com.github.mohrezal.api.domains.notifications.mappers;

import com.github.mohrezal.api.domains.notifications.dtos.ActorSummary;
import com.github.mohrezal.api.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.api.domains.notifications.models.Notification;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.users.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {StorageMapper.class})
public interface NotificationMapper {

    @Mapping(target = "actor", source = "actor")
    NotificationSummary toNotificationSummary(Notification notification);

    @Mapping(target = "avatar", source = "avatar")
    ActorSummary toActorSummary(User user);
}
