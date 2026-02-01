package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class MarkNotificationReadCommandParams {
    private UUID notificationId;
    private UserDetails userDetails;
}
