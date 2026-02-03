package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import org.springframework.http.HttpStatus;

public class InternalException extends BaseException {
    public InternalException(MessageKey messageKey) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, messageKey);
    }

    public InternalException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, MessageKey.SHARED_ERROR_INTERNAL);
    }
}
