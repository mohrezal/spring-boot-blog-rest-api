package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;

public class UserAlreadyFollowingException extends InvalidRequestException {
    public UserAlreadyFollowingException() {
        super(MessageKey.USER_ALREADY_FOLLOWING);
    }

    public UserAlreadyFollowingException(ExceptionContext context) {
        super(MessageKey.USER_ALREADY_FOLLOWING, context);
    }

    public UserAlreadyFollowingException(ExceptionContext context, Throwable cause) {
        super(MessageKey.USER_ALREADY_FOLLOWING, context, cause);
    }
}
