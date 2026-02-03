package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BaseException {
    public ResourceConflictException() {
        super(HttpStatus.CONFLICT, MessageKey.SHARED_ERROR_RESOURCE_CONFLICT);
    }

    public ResourceConflictException(MessageKey messageKey) {
        super(HttpStatus.CONFLICT, messageKey);
    }
}
