package com.github.mohrezal.api.domains.users.commands.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record LogoutUserCommandParams(UserDetails userDetails, String refreshToken)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
