package com.github.mohrezal.api.domains.notifications.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class NotificationNotFoundException extends ResourceNotFoundException {
    public NotificationNotFoundException() {
        super(MessageKey.NOTIFICATION_NOT_FOUND);
    }

    public NotificationNotFoundException(ExceptionContext context) {
        super(MessageKey.NOTIFICATION_NOT_FOUND, context);
    }

    public NotificationNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.NOTIFICATION_NOT_FOUND, context, cause);
    }
}
