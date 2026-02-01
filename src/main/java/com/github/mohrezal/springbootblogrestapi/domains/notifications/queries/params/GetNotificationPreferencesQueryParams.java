package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetNotificationPreferencesQueryParams {
    private UserDetails userDetails;
}
