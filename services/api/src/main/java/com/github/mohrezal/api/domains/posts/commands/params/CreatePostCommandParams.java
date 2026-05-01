package com.github.mohrezal.api.domains.posts.commands.params;

import com.github.mohrezal.api.domains.posts.dtos.CreatePostRequest;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record CreatePostCommandParams(UserDetails userDetails, CreatePostRequest createPostRequest)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
