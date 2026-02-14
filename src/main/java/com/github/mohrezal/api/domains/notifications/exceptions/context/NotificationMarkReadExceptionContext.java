package com.github.mohrezal.api.domains.notifications.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record NotificationMarkReadExceptionContext(String userId, String notificationId)
        implements ExceptionContext {}
