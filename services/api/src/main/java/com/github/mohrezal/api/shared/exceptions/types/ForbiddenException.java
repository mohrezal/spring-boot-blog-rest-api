package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String messageKey) {
        super(messageKey, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException() {
        super(MessageKey.SHARED_ERROR_FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(ExceptionContext context) {
        super(MessageKey.SHARED_ERROR_FORBIDDEN, HttpStatus.FORBIDDEN, context);
    }

    public ForbiddenException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.FORBIDDEN, context);
    }

    public ForbiddenException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.FORBIDDEN, context, cause);
    }
}
