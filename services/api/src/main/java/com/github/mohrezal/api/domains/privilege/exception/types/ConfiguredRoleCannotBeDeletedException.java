package com.github.mohrezal.api.domains.privilege.exception.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.common.constants.MessageKey;

public class ConfiguredRoleCannotBeDeletedException extends ResourceConflictException {
    public ConfiguredRoleCannotBeDeletedException() {
        super(MessageKey.Privilege.Error.CONFIGURED_ROLE_CANNOT_BE_DELETED);
    }

    public ConfiguredRoleCannotBeDeletedException(ExceptionContext context) {
        super(MessageKey.Privilege.Error.CONFIGURED_ROLE_CANNOT_BE_DELETED, context);
    }

    public ConfiguredRoleCannotBeDeletedException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Privilege.Error.CONFIGURED_ROLE_CANNOT_BE_DELETED, context, cause);
    }
}
