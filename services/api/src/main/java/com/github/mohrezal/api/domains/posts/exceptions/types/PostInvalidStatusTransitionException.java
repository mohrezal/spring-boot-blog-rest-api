package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;

public class PostInvalidStatusTransitionException extends InvalidRequestException {
    public PostInvalidStatusTransitionException() {
        super(MessageKey.POST_STATUS_TRANSITION_INVALID);
    }

    public PostInvalidStatusTransitionException(ExceptionContext context) {
        super(MessageKey.POST_STATUS_TRANSITION_INVALID, context);
    }

    public PostInvalidStatusTransitionException(ExceptionContext context, Throwable cause) {
        super(MessageKey.POST_STATUS_TRANSITION_INVALID, context, cause);
    }
}
