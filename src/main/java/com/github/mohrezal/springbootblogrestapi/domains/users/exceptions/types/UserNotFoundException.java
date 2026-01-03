package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, MessageKey.USER_NOT_FOUND);
    }

    public UserNotFoundException(MessageKey messageKey) {
        super(HttpStatus.NOT_FOUND, messageKey);
    }
}
