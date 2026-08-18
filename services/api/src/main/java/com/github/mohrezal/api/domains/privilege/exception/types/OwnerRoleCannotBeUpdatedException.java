package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class OwnerRoleCannotBeUpdatedException extends ResourceConflictException {
    public OwnerRoleCannotBeUpdatedException() {
        super(MessageKey.Privilege.Error.OWNER_ROLE_CANNOT_BE_UPDATED);
    }

    public OwnerRoleCannotBeUpdatedException(ExceptionContext context) {
        super(MessageKey.Privilege.Error.OWNER_ROLE_CANNOT_BE_UPDATED, context);
    }

    public OwnerRoleCannotBeUpdatedException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Privilege.Error.OWNER_ROLE_CANNOT_BE_UPDATED, context, cause);
    }
}
