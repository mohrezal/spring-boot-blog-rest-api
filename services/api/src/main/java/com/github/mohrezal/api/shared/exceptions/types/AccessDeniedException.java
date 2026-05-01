package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public class AccessDeniedException extends ForbiddenException {
    public AccessDeniedException() {
        super(MessageKey.SHARED_ERROR_ACCESS_DENIED);
    }

    public AccessDeniedException(ExceptionContext context) {
        super(MessageKey.SHARED_ERROR_ACCESS_DENIED, context);
    }

    public AccessDeniedException(ExceptionContext context, Throwable cause) {
        super(MessageKey.SHARED_ERROR_ACCESS_DENIED, context, cause);
    }
}
