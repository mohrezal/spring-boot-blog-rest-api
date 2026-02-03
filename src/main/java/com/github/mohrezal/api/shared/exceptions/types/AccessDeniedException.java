package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;

public class AccessDeniedException extends ForbiddenException {
    public AccessDeniedException() {
        super(MessageKey.SHARED_ERROR_ACCESS_DENIED);
    }
}
