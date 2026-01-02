package com.github.mohrezal.springbootblogrestapi.shared.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;

public class AccessDeniedException extends ForbiddenException {
    public AccessDeniedException() {
        super(MessageKey.SHARED_ERROR_ACCESS_DENIED);
    }
}
