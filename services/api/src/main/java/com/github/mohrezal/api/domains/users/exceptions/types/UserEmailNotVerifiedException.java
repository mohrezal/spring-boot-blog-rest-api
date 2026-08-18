package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.domains.users.exceptions.context.UserLoginExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserEmailNotVerifiedException extends ForbiddenException {
    public UserEmailNotVerifiedException() {
        super(MessageKey.User.Error.EMAIL_NOT_VERIFIED);
    }

    public UserEmailNotVerifiedException(UserLoginExceptionContext context) {
        super(MessageKey.User.Error.EMAIL_NOT_VERIFIED, context);
    }
}
