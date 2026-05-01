package com.github.mohrezal.api.domains.notifications.data;

import java.util.UUID;

public record FollowNotificationData(UUID actorId) implements NotificationData {}
