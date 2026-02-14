package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.context.NoExceptionContext;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String messageKey;
    private final ExceptionContext context;

    protected BaseException(String messageKey, HttpStatus statusCode) {
        super(messageKey);
        this.messageKey = messageKey;
        this.statusCode = statusCode;
        this.context = NoExceptionContext.INSTANCE;
    }

    protected BaseException(String messageKey, HttpStatus statusCode, ExceptionContext context) {
        super(messageKey);
        this.messageKey = messageKey;
        this.statusCode = statusCode;
        this.context = context;
    }

    protected BaseException(
            String messageKey, HttpStatus statusCode, ExceptionContext context, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.statusCode = statusCode;
        this.context = context;
    }
}
