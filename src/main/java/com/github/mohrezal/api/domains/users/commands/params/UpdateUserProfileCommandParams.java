package com.github.mohrezal.api.domains.users.commands.params;

import com.github.mohrezal.api.domains.users.dtos.UpdateUserProfileRequest;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record UpdateUserProfileCommandParams(
        UserDetails userDetails, UpdateUserProfileRequest request) implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
