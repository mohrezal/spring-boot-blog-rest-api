package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException() {
        super(MessageKey.SHARED_ERROR_RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String messageKey) {
        super(messageKey, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String messageKey, ExceptionContext context) {
        super(messageKey, HttpStatus.NOT_FOUND, context);
    }

    public ResourceNotFoundException(String messageKey, ExceptionContext context, Throwable cause) {
        super(messageKey, HttpStatus.NOT_FOUND, context, cause);
    }
}
