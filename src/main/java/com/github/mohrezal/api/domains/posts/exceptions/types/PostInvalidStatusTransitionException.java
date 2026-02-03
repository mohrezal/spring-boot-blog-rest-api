package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;

public class PostInvalidStatusTransitionException extends InvalidRequestException {
    public PostInvalidStatusTransitionException() {
        super(MessageKey.POST_STATUS_TRANSITION_INVALID);
    }
}
