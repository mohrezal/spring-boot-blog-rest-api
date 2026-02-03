package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND, MessageKey.SHARED_ERROR_RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(MessageKey messageKey) {
        super(HttpStatus.NOT_FOUND, messageKey);
    }
}
