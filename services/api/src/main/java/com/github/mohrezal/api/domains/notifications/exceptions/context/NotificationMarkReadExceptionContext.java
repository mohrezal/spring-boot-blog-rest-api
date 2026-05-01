package com.github.mohrezal.api.domains.notifications.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import java.util.UUID;

public record NotificationMarkReadExceptionContext(UUID userId, String notificationId)
        implements ExceptionContext {}
