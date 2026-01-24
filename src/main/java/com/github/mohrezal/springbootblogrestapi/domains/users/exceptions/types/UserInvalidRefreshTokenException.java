package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.UnauthorizedException;

public class UserInvalidRefreshTokenException extends UnauthorizedException {
    public UserInvalidRefreshTokenException() {
        super(MessageKey.USER_INVALID_REFRESH_TOKEN);
    }

    public UserInvalidRefreshTokenException(MessageKey messageKey) {
        super(messageKey);
    }
}
