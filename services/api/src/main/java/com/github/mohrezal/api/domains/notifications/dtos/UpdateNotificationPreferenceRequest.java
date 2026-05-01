package com.github.mohrezal.api.domains.notifications.dtos;

import lombok.Builder;

@Builder
public record UpdateNotificationPreferenceRequest(boolean inAppEnabled, boolean emailEnabled) {}
