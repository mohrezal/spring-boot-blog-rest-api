package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.BaseException;
import org.springframework.http.HttpStatus;

public class UserInvalidRefreshTokenException extends BaseException {
    public UserInvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, MessageKey.USER_INVALID_REFRESH_TOKEN);
    }

    public UserInvalidRefreshTokenException(MessageKey messageKey) {
        super(HttpStatus.UNAUTHORIZED, messageKey);
    }
}
