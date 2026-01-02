package com.github.mohrezal.springbootblogrestapi.shared.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BaseException {
    public ResourceConflictException() {
        super(HttpStatus.CONFLICT, MessageKey.SHARED_ERROR_RESOURCE_CONFLICT);
    }

    public ResourceConflictException(MessageKey messageKey) {
        super(HttpStatus.CONFLICT, messageKey);
    }
}
