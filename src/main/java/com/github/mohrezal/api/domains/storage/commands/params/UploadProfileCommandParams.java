package com.github.mohrezal.api.domains.storage.commands.params;

import com.github.mohrezal.api.domains.storage.dtos.UploadProfileRequest;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record UploadProfileCommandParams(
        UserDetails userDetails, UploadProfileRequest uploadProfileRequest)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
