package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException() {
        super(MessageKey.SHARED_ERROR_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String messageKey) {
        super(messageKey, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.UNAUTHORIZED, context);
    }

    public UnauthorizedException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.UNAUTHORIZED, context, cause);
    }
}
