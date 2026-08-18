package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.common.constants.MessageKey;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException() {
        super(MessageKey.Privilege.Error.ROLE_NOT_FOUND);
    }

    public RoleNotFoundException(ExceptionContext context) {
        super(MessageKey.Privilege.Error.ROLE_NOT_FOUND, context);
    }

    public RoleNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Privilege.Error.ROLE_NOT_FOUND, context, cause);
    }
}
