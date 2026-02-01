package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.UpdateNotificationPreferenceRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class UpdateNotificationPreferencesCommandParams {
    private UserDetails userDetails;
    private UpdateNotificationPreferenceRequest request;
}
