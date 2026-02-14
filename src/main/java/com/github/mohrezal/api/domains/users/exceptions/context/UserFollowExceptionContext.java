package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record UserFollowExceptionContext(String userId, String targetHandle)
        implements ExceptionContext {}
