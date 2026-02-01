package com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos;

import lombok.Builder;

@Builder
public record NotificationPreferenceSummary(boolean inAppEnabled, boolean emailEnabled) {}
