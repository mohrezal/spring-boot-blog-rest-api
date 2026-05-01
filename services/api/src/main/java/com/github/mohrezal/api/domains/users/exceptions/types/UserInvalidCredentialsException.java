package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.domains.users.exceptions.context.UserLoginExceptionContext;
import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;

public class UserInvalidCredentialsException extends UnauthorizedException {
    public UserInvalidCredentialsException() {
        super(MessageKey.USER_INVALID_CREDENTIALS);
    }

    public UserInvalidCredentialsException(String messageKey) {
        super(messageKey);
    }

    public UserInvalidCredentialsException(UserLoginExceptionContext context, Throwable cause) {
        super(MessageKey.USER_INVALID_CREDENTIALS, context, cause);
    }
}
