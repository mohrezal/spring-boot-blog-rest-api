package com.github.mohrezal.api.domains.notifications.dtos;

import lombok.Builder;

@Builder
public record NotificationPreferenceSummary(boolean inAppEnabled, boolean emailEnabled) {}
