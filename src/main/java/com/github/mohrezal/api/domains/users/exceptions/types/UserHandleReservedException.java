package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.domains.users.exceptions.context.UserRegisterExceptionContext;
import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;

public class UserHandleReservedException extends ResourceConflictException {
    public UserHandleReservedException() {
        super(MessageKey.USER_HANDLE_RESERVED);
    }

    public UserHandleReservedException(UserRegisterExceptionContext context) {
        super(MessageKey.USER_HANDLE_RESERVED, context);
    }
}
