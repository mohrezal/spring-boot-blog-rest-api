package com.github.mohrezal.springbootblogrestapi.domains.notifications.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;

public class NotificationPreferencesNotFoundException extends ResourceNotFoundException {
    public NotificationPreferencesNotFoundException() {
        super(MessageKey.NOTIFICATION_PREFERENCE_NOT_FOUND);
    }
}
