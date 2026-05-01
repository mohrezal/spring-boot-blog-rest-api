package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class PostNotFoundException extends ResourceNotFoundException {
    public PostNotFoundException() {
        super(MessageKey.POST_NOT_FOUND);
    }

    public PostNotFoundException(ExceptionContext context) {
        super(MessageKey.POST_NOT_FOUND, context);
    }

    public PostNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.POST_NOT_FOUND, context, cause);
    }
}
