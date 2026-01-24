package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;

public class UserEmailAlreadyExistsException extends ResourceConflictException {
    public UserEmailAlreadyExistsException() {
        super(MessageKey.USER_EMAIL_ALREADY_EXISTS);
    }

    public UserEmailAlreadyExistsException(MessageKey messageKey) {
        super(messageKey);
    }
}
