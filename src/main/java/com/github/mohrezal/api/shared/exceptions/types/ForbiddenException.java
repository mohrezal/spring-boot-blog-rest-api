package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String messageKey) {
        super(messageKey, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException() {
        super(MessageKey.SHARED_ERROR_FORBIDDEN, HttpStatus.FORBIDDEN);
    }
}
