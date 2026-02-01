package com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.data.NotificationData;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationSummary(
        UUID id,
        NotificationData data,
        ActorSummary actor,
        Boolean isRead,
        OffsetDateTime readAt,
        OffsetDateTime createdAt) {}
