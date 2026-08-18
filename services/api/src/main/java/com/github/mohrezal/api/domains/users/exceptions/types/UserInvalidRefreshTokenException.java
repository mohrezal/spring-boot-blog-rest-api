package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserInvalidRefreshTokenException extends UnauthorizedException {
    public UserInvalidRefreshTokenException() {
        super(MessageKey.User.Error.INVALID_REFRESH_TOKEN);
    }

    public UserInvalidRefreshTokenException(String messageKey) {
        super(messageKey);
    }

    public UserInvalidRefreshTokenException(ExceptionContext context) {
        super(MessageKey.User.Error.INVALID_REFRESH_TOKEN, context);
    }

    public UserInvalidRefreshTokenException(ExceptionContext context, Throwable cause) {
        super(MessageKey.User.Error.INVALID_REFRESH_TOKEN, context, cause);
    }
}
