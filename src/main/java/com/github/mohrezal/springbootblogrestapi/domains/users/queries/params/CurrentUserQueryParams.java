package com.github.mohrezal.springbootblogrestapi.domains.users.queries.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class CurrentUserQueryParams {
    private final UserDetails userDetails;
}
