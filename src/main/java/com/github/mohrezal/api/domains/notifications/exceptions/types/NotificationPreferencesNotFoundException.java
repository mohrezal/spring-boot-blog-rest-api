package com.github.mohrezal.api.domains.notifications.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class NotificationPreferencesNotFoundException extends ResourceNotFoundException {
    public NotificationPreferencesNotFoundException() {
        super(MessageKey.NOTIFICATION_PREFERENCE_NOT_FOUND);
    }

    public NotificationPreferencesNotFoundException(ExceptionContext context) {
        super(MessageKey.NOTIFICATION_PREFERENCE_NOT_FOUND, context);
    }

    public NotificationPreferencesNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.NOTIFICATION_PREFERENCE_NOT_FOUND, context, cause);
    }
}
