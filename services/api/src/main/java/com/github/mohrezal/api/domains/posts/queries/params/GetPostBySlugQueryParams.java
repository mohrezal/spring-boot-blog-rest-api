package com.github.mohrezal.api.domains.posts.queries.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record GetPostBySlugQueryParams(UserDetails userDetails, String slug)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
