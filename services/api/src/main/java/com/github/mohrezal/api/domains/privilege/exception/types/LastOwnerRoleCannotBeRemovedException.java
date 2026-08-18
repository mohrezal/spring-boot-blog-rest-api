package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class LastOwnerRoleCannotBeRemovedException extends ResourceConflictException {
    public LastOwnerRoleCannotBeRemovedException() {
        super(MessageKey.Privilege.Error.LAST_OWNER_ROLE_CANNOT_BE_REMOVED);
    }

    public LastOwnerRoleCannotBeRemovedException(ExceptionContext context) {
        super(MessageKey.Privilege.Error.LAST_OWNER_ROLE_CANNOT_BE_REMOVED, context);
    }

    public LastOwnerRoleCannotBeRemovedException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Privilege.Error.LAST_OWNER_ROLE_CANNOT_BE_REMOVED, context, cause);
    }
}
