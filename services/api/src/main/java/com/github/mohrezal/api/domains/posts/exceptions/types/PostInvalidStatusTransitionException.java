package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class PostInvalidStatusTransitionException extends InvalidRequestException {
    public PostInvalidStatusTransitionException() {
        super(MessageKey.Post.Error.STATUS_TRANSITION_INVALID);
    }

    public PostInvalidStatusTransitionException(ExceptionContext context) {
        super(MessageKey.Post.Error.STATUS_TRANSITION_INVALID, context);
    }

    public PostInvalidStatusTransitionException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Post.Error.STATUS_TRANSITION_INVALID, context, cause);
    }
}
