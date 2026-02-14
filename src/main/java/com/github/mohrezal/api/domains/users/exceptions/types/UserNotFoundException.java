package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super(MessageKey.USER_NOT_FOUND);
    }

    public UserNotFoundException(String messageKey) {
        super(messageKey);
    }

    public UserNotFoundException(ExceptionContext context) {
        super(MessageKey.USER_NOT_FOUND, context);
    }

    public UserNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.USER_NOT_FOUND, context, cause);
    }
}
