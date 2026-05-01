package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException() {
        super(MessageKey.SHARED_ERROR_INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestException(String messageKey) {
        super(messageKey, HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestException(ExceptionContext context) {
        super(MessageKey.SHARED_ERROR_INVALID_REQUEST, HttpStatus.BAD_REQUEST, context);
    }

    public InvalidRequestException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.BAD_REQUEST, context);
    }

    public InvalidRequestException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.BAD_REQUEST, context, cause);
    }
}
