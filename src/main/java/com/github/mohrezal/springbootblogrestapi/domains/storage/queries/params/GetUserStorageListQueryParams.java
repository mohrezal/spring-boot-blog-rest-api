package com.github.mohrezal.springbootblogrestapi.domains.storage.queries.params;

import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record GetUserStorageListQueryParams(UserDetails userDetails, int page, int size)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
