package com.github.mohrezal.api.domains.notifications.commands.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record MarkAllNotificationsReadCommandParams(UserDetails userDetails)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
