package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;

public class PostSlugAlreadyExistsException extends ResourceConflictException {
    public PostSlugAlreadyExistsException() {
        super(MessageKey.POST_SLUG_ALREADY_EXISTS);
    }

    public PostSlugAlreadyExistsException(ExceptionContext context) {
        super(MessageKey.POST_SLUG_ALREADY_EXISTS, context);
    }

    public PostSlugAlreadyExistsException(ExceptionContext context, Throwable cause) {
        super(MessageKey.POST_SLUG_ALREADY_EXISTS, context, cause);
    }
}
