package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.common.constants.MessageKey;
import org.springframework.http.HttpStatus;

public class InternalException extends BaseException {
    public InternalException(String messageKey) {
        super(messageKey, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalException() {
        super(MessageKey.Shared.Error.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalException(ExceptionContext context) {
        super(MessageKey.Shared.Error.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR, context);
    }

    public InternalException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.INTERNAL_SERVER_ERROR, context);
    }

    public InternalException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.INTERNAL_SERVER_ERROR, context, cause);
    }
}
