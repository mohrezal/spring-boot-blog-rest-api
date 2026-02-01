package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.UpdateNotificationPreferenceRequest;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
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
