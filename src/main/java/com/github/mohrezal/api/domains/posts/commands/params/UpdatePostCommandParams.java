package com.github.mohrezal.api.domains.posts.commands.params;

import com.github.mohrezal.api.domains.posts.dtos.UpdatePostRequest;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record UpdatePostCommandParams(
        UserDetails userDetails, UpdatePostRequest updatePostRequest, String slug)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
