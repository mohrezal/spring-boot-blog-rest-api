package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.common.constants.MessageKey;

public class AccessDeniedException extends ForbiddenException {
    public AccessDeniedException() {
        super(MessageKey.Shared.Error.ACCESS_DENIED);
    }

    public AccessDeniedException(ExceptionContext context) {
        super(MessageKey.Shared.Error.ACCESS_DENIED, context);
    }

    public AccessDeniedException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Shared.Error.ACCESS_DENIED, context, cause);
    }
}
