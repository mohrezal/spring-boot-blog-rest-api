package com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;

public class PostInvalidStatusTransitionException extends InvalidRequestException {
    public PostInvalidStatusTransitionException() {
        super(MessageKey.POST_STATUS_TRANSITION_INVALID);
    }
}
