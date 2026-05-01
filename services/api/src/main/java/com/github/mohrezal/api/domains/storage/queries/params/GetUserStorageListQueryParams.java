package com.github.mohrezal.api.domains.storage.queries.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record GetUserStorageListQueryParams(UserDetails userDetails, int page, int size)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
