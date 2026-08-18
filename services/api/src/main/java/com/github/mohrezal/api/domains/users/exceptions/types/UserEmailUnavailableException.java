package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserEmailUnavailableException extends ResourceConflictException {
    public UserEmailUnavailableException() {
        super(MessageKey.User.Error.EMAIL_UNAVAILABLE);
    }

    public UserEmailUnavailableException(ExceptionContext context) {
        super(MessageKey.User.Error.EMAIL_UNAVAILABLE, context);
    }

    public UserEmailUnavailableException(ExceptionContext context, Throwable cause) {
        super(MessageKey.User.Error.EMAIL_UNAVAILABLE, context, cause);
    }
}
