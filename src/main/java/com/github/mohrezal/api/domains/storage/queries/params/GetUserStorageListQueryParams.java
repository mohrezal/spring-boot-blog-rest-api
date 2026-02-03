package com.github.mohrezal.api.domains.storage.queries.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
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
