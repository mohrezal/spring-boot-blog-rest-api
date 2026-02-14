package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record UserLogoutExceptionContext(String userId, boolean hasRefreshToken)
        implements ExceptionContext {}
