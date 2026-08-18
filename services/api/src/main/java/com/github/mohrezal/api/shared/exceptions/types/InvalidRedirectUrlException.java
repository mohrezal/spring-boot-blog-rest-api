package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.common.constants.MessageKey;
import org.springframework.http.HttpStatus;

public class InvalidRedirectUrlException extends BaseException {

    public InvalidRedirectUrlException() {
        super(MessageKey.Shared.Error.INVALID_REDIRECT_URL, HttpStatus.BAD_REQUEST);
    }

    public InvalidRedirectUrlException(String messageKey) {
        super(messageKey, HttpStatus.BAD_REQUEST);
    }

    public InvalidRedirectUrlException(ExceptionContext context) {
        super(MessageKey.Shared.Error.INVALID_REDIRECT_URL, HttpStatus.BAD_REQUEST, context);
    }

    public InvalidRedirectUrlException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.BAD_REQUEST, context);
    }

    public InvalidRedirectUrlException(
            String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.BAD_REQUEST, context, cause);
    }
}
