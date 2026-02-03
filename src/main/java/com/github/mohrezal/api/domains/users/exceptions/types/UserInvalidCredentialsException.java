package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;

public class UserInvalidCredentialsException extends UnauthorizedException {
    public UserInvalidCredentialsException() {
        super(MessageKey.USER_INVALID_CREDENTIALS);
    }

    public UserInvalidCredentialsException(MessageKey messageKey) {
        super(messageKey);
    }
}
