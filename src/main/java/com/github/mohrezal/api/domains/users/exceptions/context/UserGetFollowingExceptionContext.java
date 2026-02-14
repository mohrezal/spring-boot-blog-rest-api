package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record UserGetFollowingExceptionContext(
        String userId, String handle, String targetHandle, int page, int size)
        implements ExceptionContext {}
