package com.github.mohrezal.springbootblogrestapi.shared.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException() {
        super(HttpStatus.BAD_REQUEST, MessageKey.SHARED_ERROR_INVALID_REQUEST);
    }

    public InvalidRequestException(MessageKey messageKey) {
        super(HttpStatus.BAD_REQUEST, messageKey);
    }
}
