package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends BaseException {
    public ResourceConflictException() {
        super(MessageKey.SHARED_ERROR_RESOURCE_CONFLICT, HttpStatus.CONFLICT);
    }

    public ResourceConflictException(String messageKey) {
        super(messageKey, HttpStatus.CONFLICT);
    }

    public ResourceConflictException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.CONFLICT, context);
    }

    public ResourceConflictException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.CONFLICT, context, cause);
    }
}
