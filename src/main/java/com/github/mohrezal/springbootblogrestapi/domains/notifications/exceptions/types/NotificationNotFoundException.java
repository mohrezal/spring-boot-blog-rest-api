package com.github.mohrezal.springbootblogrestapi.domains.notifications.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;

public class NotificationNotFoundException extends ResourceNotFoundException {
    public NotificationNotFoundException() {
        super(MessageKey.NOTIFICATION_NOT_FOUND);
    }
}
