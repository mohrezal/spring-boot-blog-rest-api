package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserNotFollowingException extends InvalidRequestException {
    public UserNotFollowingException() {
        super(MessageKey.USER_NOT_FOLLOWING);
    }

    public UserNotFollowingException(ExceptionContext context) {
        super(MessageKey.USER_NOT_FOLLOWING, context);
    }

    public UserNotFollowingException(ExceptionContext context, Throwable cause) {
        super(MessageKey.USER_NOT_FOLLOWING, context, cause);
    }
}
