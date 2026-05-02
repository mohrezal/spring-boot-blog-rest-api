package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.domains.users.exceptions.context.UserRegisterExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserHandleAlreadyExistsException extends ResourceConflictException {
    public UserHandleAlreadyExistsException() {
        super(MessageKey.USER_HANDLE_ALREADY_EXISTS);
    }

    public UserHandleAlreadyExistsException(UserRegisterExceptionContext context) {
        super(MessageKey.USER_HANDLE_ALREADY_EXISTS, context);
    }
}
