package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;

public class UserEmailConflictException extends ResourceConflictException {
    public UserEmailConflictException() {
        super(MessageKey.USER_ERROR_EMAIL_CONFLICT);
    }
}
