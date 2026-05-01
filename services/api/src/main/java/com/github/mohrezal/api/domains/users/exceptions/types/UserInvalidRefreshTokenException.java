package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;

public class UserInvalidRefreshTokenException extends UnauthorizedException {
    public UserInvalidRefreshTokenException() {
        super(MessageKey.USER_INVALID_REFRESH_TOKEN);
    }

    public UserInvalidRefreshTokenException(String messageKey) {
        super(messageKey);
    }

    public UserInvalidRefreshTokenException(ExceptionContext context) {
        super(MessageKey.USER_INVALID_REFRESH_TOKEN, context);
    }

    public UserInvalidRefreshTokenException(ExceptionContext context, Throwable cause) {
        super(MessageKey.USER_INVALID_REFRESH_TOKEN, context, cause);
    }
}
