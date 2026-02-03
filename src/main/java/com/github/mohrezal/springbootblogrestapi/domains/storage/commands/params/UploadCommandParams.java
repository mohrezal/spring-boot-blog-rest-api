package com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params;

import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.springbootblogrestapi.domains.storage.enums.StorageType;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record UploadCommandParams(
        UserDetails userDetails, UploadRequest uploadRequest, StorageType type)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
