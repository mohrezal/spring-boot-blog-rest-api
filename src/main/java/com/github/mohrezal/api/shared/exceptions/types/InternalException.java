package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class InternalException extends BaseException {
    public InternalException(String messageKey) {
        super(messageKey, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalException() {
        super(MessageKey.SHARED_ERROR_INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
