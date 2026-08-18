package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserHandleUnavailableException extends ResourceConflictException {
    public UserHandleUnavailableException() {
        super(MessageKey.USER_HANDLE_UNAVAILABLE);
    }

    public UserHandleUnavailableException(ExceptionContext context) {
        super(MessageKey.USER_HANDLE_UNAVAILABLE, context);
    }

    public UserHandleUnavailableException(ExceptionContext context, Throwable cause) {
        super(MessageKey.USER_HANDLE_UNAVAILABLE, context, cause);
    }
}
