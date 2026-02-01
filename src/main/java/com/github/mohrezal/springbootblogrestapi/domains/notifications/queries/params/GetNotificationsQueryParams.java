package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class GetNotificationsQueryParams {
    private UserDetails userDetails;
    private int page;
    private int size;
}
