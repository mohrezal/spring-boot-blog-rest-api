package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.domains.users.exceptions.context.UserRegisterExceptionContext;
import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;

public class UserEmailAlreadyExistsException extends ResourceConflictException {
    public UserEmailAlreadyExistsException() {
        super(MessageKey.USER_EMAIL_ALREADY_EXISTS);
    }

    public UserEmailAlreadyExistsException(String messageKey) {
        super(messageKey);
    }

    public UserEmailAlreadyExistsException(UserRegisterExceptionContext context) {
        super(MessageKey.USER_EMAIL_ALREADY_EXISTS, context);
    }

    public UserEmailAlreadyExistsException(UserRegisterExceptionContext context, Throwable cause) {
        super(MessageKey.USER_EMAIL_ALREADY_EXISTS, context, cause);
    }
}
