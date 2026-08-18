package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class PostSlugAlreadyExistsException extends ResourceConflictException {
    public PostSlugAlreadyExistsException() {
        super(MessageKey.Post.Error.SLUG_ALREADY_EXISTS);
    }

    public PostSlugAlreadyExistsException(ExceptionContext context) {
        super(MessageKey.Post.Error.SLUG_ALREADY_EXISTS, context);
    }

    public PostSlugAlreadyExistsException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Post.Error.SLUG_ALREADY_EXISTS, context, cause);
    }
}
