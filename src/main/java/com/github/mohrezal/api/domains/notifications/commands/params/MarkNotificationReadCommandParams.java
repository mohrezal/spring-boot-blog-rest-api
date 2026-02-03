package com.github.mohrezal.api.domains.notifications.commands.params;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
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
