package com.github.mohrezal.api.domains.users.queries.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record GetUserFollowersQueryParams(
        UserDetails userDetails, String handle, int page, int size) implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
