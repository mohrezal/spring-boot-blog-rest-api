package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record UserFollowExceptionContext(
        String followerId, String followerHandle, String targetHandle)
        implements ExceptionContext {}
