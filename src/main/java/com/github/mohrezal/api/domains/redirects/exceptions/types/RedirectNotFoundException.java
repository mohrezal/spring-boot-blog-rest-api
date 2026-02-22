package com.github.mohrezal.api.domains.redirects.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class RedirectNotFoundException extends ResourceNotFoundException {
    public RedirectNotFoundException() {
        super(MessageKey.REDIRECT_NOT_FOUND);
    }

    public RedirectNotFoundException(ExceptionContext context) {
        super(MessageKey.REDIRECT_NOT_FOUND, context);
    }

    public RedirectNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.REDIRECT_NOT_FOUND, context, cause);
    }
}
