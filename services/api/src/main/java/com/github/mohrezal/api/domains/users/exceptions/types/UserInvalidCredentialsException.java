package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.domains.users.exceptions.context.UserLoginExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserInvalidCredentialsException extends UnauthorizedException {
    public UserInvalidCredentialsException() {
        super(MessageKey.User.Error.INVALID_CREDENTIALS);
    }

    public UserInvalidCredentialsException(String messageKey) {
        super(messageKey);
    }

    public UserInvalidCredentialsException(UserLoginExceptionContext context, Throwable cause) {
        super(MessageKey.User.Error.INVALID_CREDENTIALS, context, cause);
    }
}
