package com.github.mohrezal.api.domains.privilege.exception;

import com.github.mohrezal.api.domains.privilege.exception.types.ConfiguredRoleCannotBeDeletedException;
import com.github.mohrezal.api.domains.privilege.exception.types.LastOwnerRoleCannotBeRemovedException;
import com.github.mohrezal.api.domains.privilege.exception.types.OwnerRoleCannotBeUpdatedException;
import com.github.mohrezal.api.domains.privilege.exception.types.PermissionNotFoundException;
import com.github.mohrezal.api.domains.privilege.exception.types.ProtectedPermissionCannotBeDisabledException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleAssignedToUsersException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleKeyAlreadyExistsException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleNotFoundException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
import com.github.mohrezal.api.shared.exceptions.types.BaseException;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PrivilegeExceptionHandler extends AbstractExceptionHandler {

    public PrivilegeExceptionHandler(MessageSource messageSource, CookieUtils cookieUtils) {
        super(messageSource, cookieUtils);
    }

    @ExceptionHandler({
        ConfiguredRoleCannotBeDeletedException.class,
        LastOwnerRoleCannotBeRemovedException.class,
        OwnerRoleCannotBeUpdatedException.class,
        PermissionNotFoundException.class,
        ProtectedPermissionCannotBeDisabledException.class,
        RoleAssignedToUsersException.class,
        RoleKeyAlreadyExistsException.class,
        RoleNotFoundException.class
    })
    public ResponseEntity<@NonNull ErrorResponse> handlePrivilegeException(BaseException ex) {
        return buildErrorResponse(ex);
    }
}
