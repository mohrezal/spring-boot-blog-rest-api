package com.github.mohrezal.springbootblogrestapi.shared.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(MessageKey messageKey) {
        super(HttpStatus.FORBIDDEN, messageKey);
    }

    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, MessageKey.SHARED_ERROR_FORBIDDEN);
    }
}
