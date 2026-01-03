package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.BaseException;
import org.springframework.http.HttpStatus;

public class UserRefreshTokenNotFoundException extends BaseException {
    public UserRefreshTokenNotFoundException() {
        super(HttpStatus.NOT_FOUND, MessageKey.USER_REFRESH_TOKEN_NOT_FOUND);
    }

    public UserRefreshTokenNotFoundException(MessageKey messageKey) {
        super(HttpStatus.NOT_FOUND, messageKey);
    }
}
