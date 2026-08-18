package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.common.constants.MessageKey;

public class PostNotFoundException extends ResourceNotFoundException {
    public PostNotFoundException() {
        super(MessageKey.Post.Error.NOT_FOUND);
    }

    public PostNotFoundException(ExceptionContext context) {
        super(MessageKey.Post.Error.NOT_FOUND, context);
    }

    public PostNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Post.Error.NOT_FOUND, context, cause);
    }
}
