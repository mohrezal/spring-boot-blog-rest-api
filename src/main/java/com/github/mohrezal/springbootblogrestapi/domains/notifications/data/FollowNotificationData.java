package com.github.mohrezal.springbootblogrestapi.domains.notifications.data;

import java.util.UUID;

public record FollowNotificationData(UUID actorId) implements NotificationData {}
