package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class RoleAssignedToUsersException extends ResourceConflictException {
    public RoleAssignedToUsersException() {
        super(MessageKey.Privilege.Error.ROLE_ASSIGNED_TO_USERS);
    }

    public RoleAssignedToUsersException(ExceptionContext context) {
        super(MessageKey.Privilege.Error.ROLE_ASSIGNED_TO_USERS, context);
    }

    public RoleAssignedToUsersException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Privilege.Error.ROLE_ASSIGNED_TO_USERS, context, cause);
    }
}
