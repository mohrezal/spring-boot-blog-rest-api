package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;

public class UserRefreshTokenNotFoundException extends ResourceNotFoundException {
    public UserRefreshTokenNotFoundException() {
        super(MessageKey.USER_REFRESH_TOKEN_NOT_FOUND);
    }

    public UserRefreshTokenNotFoundException(MessageKey messageKey) {
        super(messageKey);
    }
}
