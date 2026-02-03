package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class UserRefreshTokenNotFoundException extends ResourceNotFoundException {
    public UserRefreshTokenNotFoundException() {
        super(MessageKey.USER_REFRESH_TOKEN_NOT_FOUND);
    }

    public UserRefreshTokenNotFoundException(MessageKey messageKey) {
        super(messageKey);
    }
}
