package com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.data.NotificationData;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationSummary {
    private UUID id;
    private NotificationData data;
    private Boolean isRead;
    private OffsetDateTime readAt;
    private OffsetDateTime createdAt;
}
