package com.github.mohrezal.springbootblogrestapi.domains.users.queries.params;

import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record GetUserFollowingQueryParams(
        UserDetails userDetails, String handle, int page, int size) implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
