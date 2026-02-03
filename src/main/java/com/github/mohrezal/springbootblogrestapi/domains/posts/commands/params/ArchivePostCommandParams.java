package com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params;

import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record ArchivePostCommandParams(UserDetails userDetails, String slug)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
