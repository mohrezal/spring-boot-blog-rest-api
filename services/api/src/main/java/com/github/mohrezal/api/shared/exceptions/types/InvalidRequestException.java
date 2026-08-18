package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.common.constants.MessageKey;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException() {
        super(MessageKey.Shared.Error.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestException(String messageKey) {
        super(messageKey, HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestException(ExceptionContext context) {
        super(MessageKey.Shared.Error.INVALID_REQUEST, HttpStatus.BAD_REQUEST, context);
    }

    public InvalidRequestException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.BAD_REQUEST, context);
    }

    public InvalidRequestException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.BAD_REQUEST, context, cause);
    }
}
