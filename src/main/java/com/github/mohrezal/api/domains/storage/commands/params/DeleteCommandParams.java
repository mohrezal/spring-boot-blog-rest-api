package com.github.mohrezal.api.domains.storage.commands.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record DeleteCommandParams(UserDetails userDetails, String fileName)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
