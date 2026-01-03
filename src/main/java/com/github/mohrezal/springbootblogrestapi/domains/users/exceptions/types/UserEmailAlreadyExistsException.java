package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.BaseException;
import org.springframework.http.HttpStatus;

public class UserEmailAlreadyExistsException extends BaseException {
    public UserEmailAlreadyExistsException() {
        super(HttpStatus.CONFLICT, MessageKey.USER_EMAIL_ALREADY_EXISTS);
    }

    public UserEmailAlreadyExistsException(MessageKey messageKey) {
        super(HttpStatus.CONFLICT, messageKey);
    }
}
