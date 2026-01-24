package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.UnauthorizedException;

public class UserInvalidCredentialsException extends UnauthorizedException {
    public UserInvalidCredentialsException() {
        super(MessageKey.USER_INVALID_CREDENTIALS);
    }

    public UserInvalidCredentialsException(MessageKey messageKey) {
        super(messageKey);
    }
}
