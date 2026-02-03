package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, MessageKey.SHARED_ERROR_UNAUTHORIZED);
    }

    public UnauthorizedException(MessageKey messageKey) {
        super(HttpStatus.UNAUTHORIZED, messageKey);
    }
}
