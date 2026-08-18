package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserAlreadyVerifiedException extends InvalidRequestException {
    public UserAlreadyVerifiedException(ExceptionContext context) {
        super(MessageKey.User.Error.ALREADY_VERIFIED, context);
    }
}
