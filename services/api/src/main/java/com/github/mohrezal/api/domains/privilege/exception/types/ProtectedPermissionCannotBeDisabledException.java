package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class ProtectedPermissionCannotBeDisabledException extends ResourceConflictException {
    public ProtectedPermissionCannotBeDisabledException() {
        super(MessageKey.Privilege.Error.PROTECTED_PERMISSION_CANNOT_BE_DISABLED);
    }

    public ProtectedPermissionCannotBeDisabledException(ExceptionContext context) {
        super(MessageKey.Privilege.Error.PROTECTED_PERMISSION_CANNOT_BE_DISABLED, context);
    }

    public ProtectedPermissionCannotBeDisabledException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Privilege.Error.PROTECTED_PERMISSION_CANNOT_BE_DISABLED, context, cause);
    }
}
