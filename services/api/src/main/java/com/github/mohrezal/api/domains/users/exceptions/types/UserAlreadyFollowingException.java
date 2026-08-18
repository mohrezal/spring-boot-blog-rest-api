package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserAlreadyFollowingException extends InvalidRequestException {
    public UserAlreadyFollowingException() {
        super(MessageKey.User.Error.ALREADY_FOLLOWING);
    }

    public UserAlreadyFollowingException(ExceptionContext context) {
        super(MessageKey.User.Error.ALREADY_FOLLOWING, context);
    }

    public UserAlreadyFollowingException(ExceptionContext context, Throwable cause) {
        super(MessageKey.User.Error.ALREADY_FOLLOWING, context, cause);
    }
}
