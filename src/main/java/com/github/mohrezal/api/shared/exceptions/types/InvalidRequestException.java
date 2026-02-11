package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException() {
        super(MessageKey.SHARED_ERROR_INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestException(String messageKey) {
        super(messageKey, HttpStatus.BAD_REQUEST);
    }
}
