package com.github.mohrezal.api.domains.notifications.commands.params;

import com.github.mohrezal.api.domains.notifications.dtos.UpdateNotificationPreferenceRequest;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record UpdateNotificationPreferencesCommandParams(
        UserDetails userDetails, UpdateNotificationPreferenceRequest request)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
