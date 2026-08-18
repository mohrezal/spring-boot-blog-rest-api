package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.common.constants.MessageKey;

public class PermissionNotFoundException extends ResourceNotFoundException {
    public PermissionNotFoundException() {
        super(MessageKey.Privilege.Error.PERMISSION_NOT_FOUND);
    }

    public PermissionNotFoundException(ExceptionContext context) {
        super(MessageKey.Privilege.Error.PERMISSION_NOT_FOUND, context);
    }

    public PermissionNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Privilege.Error.PERMISSION_NOT_FOUND, context, cause);
    }
}
