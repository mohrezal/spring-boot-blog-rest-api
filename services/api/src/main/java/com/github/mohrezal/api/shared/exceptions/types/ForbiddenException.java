package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.common.constants.MessageKey;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String messageKey) {
        super(messageKey, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException() {
        super(MessageKey.Shared.Error.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(ExceptionContext context) {
        super(MessageKey.Shared.Error.FORBIDDEN, HttpStatus.FORBIDDEN, context);
    }

    public ForbiddenException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.FORBIDDEN, context);
    }

    public ForbiddenException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.FORBIDDEN, context, cause);
    }
}
