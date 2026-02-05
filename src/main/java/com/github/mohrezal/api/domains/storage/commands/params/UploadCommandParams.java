package com.github.mohrezal.api.domains.storage.commands.params;

import com.github.mohrezal.api.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record UploadCommandParams(
        UserDetails userDetails, UploadRequest uploadRequest, StorageType type)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
