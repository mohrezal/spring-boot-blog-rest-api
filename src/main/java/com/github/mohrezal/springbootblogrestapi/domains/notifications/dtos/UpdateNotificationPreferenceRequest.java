package com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos;

import lombok.Builder;

@Builder
public record UpdateNotificationPreferenceRequest(boolean inAppEnabled, boolean emailEnabled) {}
