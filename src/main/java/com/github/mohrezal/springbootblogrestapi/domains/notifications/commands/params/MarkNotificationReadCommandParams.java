package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params;

import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import java.util.UUID;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record MarkNotificationReadCommandParams(UUID notificationId, UserDetails userDetails)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
