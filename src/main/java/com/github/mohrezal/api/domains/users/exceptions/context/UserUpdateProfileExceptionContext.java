package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record UserUpdateProfileExceptionContext(
        String userId, String handle, boolean hasFirstName, boolean hasLastName, boolean hasBio)
        implements ExceptionContext {}
