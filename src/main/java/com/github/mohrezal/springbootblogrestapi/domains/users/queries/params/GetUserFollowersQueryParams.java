package com.github.mohrezal.springbootblogrestapi.domains.users.queries.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserFollowersQueryParams {
    private UserDetails userDetails;
    private String handle;
    private int page;
    private int size;
}
