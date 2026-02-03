package com.github.mohrezal.api.domains.posts.commands.params;

import com.github.mohrezal.api.domains.posts.dtos.UpdatePostRequest;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record UpdatePostCommandParams(
        UserDetails userDetails, UpdatePostRequest updatePostRequest, String slug)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
