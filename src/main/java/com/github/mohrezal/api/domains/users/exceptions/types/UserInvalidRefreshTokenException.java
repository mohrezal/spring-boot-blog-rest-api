package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;

public class UserInvalidRefreshTokenException extends UnauthorizedException {
    public UserInvalidRefreshTokenException() {
        super(MessageKey.USER_INVALID_REFRESH_TOKEN);
    }

    public UserInvalidRefreshTokenException(MessageKey messageKey) {
        super(messageKey);
    }
}
