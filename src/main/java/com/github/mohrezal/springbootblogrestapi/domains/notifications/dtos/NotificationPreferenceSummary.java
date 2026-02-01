package com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationPreferenceSummary {
    private boolean inAppEnabled;
    private boolean emailEnabled;
}
