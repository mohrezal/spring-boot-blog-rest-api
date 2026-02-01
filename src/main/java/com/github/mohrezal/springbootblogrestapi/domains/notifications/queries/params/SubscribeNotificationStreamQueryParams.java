package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class SubscribeNotificationStreamQueryParams {
    private UserDetails userDetails;
}
