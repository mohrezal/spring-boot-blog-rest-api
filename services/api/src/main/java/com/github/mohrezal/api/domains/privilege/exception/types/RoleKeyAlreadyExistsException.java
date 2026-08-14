package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class RoleKeyAlreadyExistsException extends ResourceConflictException {
    public RoleKeyAlreadyExistsException() {
        super(MessageKey.PRIVILEGE_ROLE_KEY_ALREADY_EXISTS);
    }

    public RoleKeyAlreadyExistsException(ExceptionContext context) {
        super(MessageKey.PRIVILEGE_ROLE_KEY_ALREADY_EXISTS, context);
    }

    public RoleKeyAlreadyExistsException(ExceptionContext context, Throwable cause) {
        super(MessageKey.PRIVILEGE_ROLE_KEY_ALREADY_EXISTS, context, cause);
    }
}
