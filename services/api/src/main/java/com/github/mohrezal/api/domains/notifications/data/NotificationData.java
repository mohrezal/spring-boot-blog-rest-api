package com.github.mohrezal.api.domains.notifications.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = FollowNotificationData.class, name = "FollowNotificationData")
})
public sealed interface NotificationData permits FollowNotificationData {}
