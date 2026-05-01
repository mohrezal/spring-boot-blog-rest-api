package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;

public class UnexpectedException extends InternalException {
    public UnexpectedException() {
        super(MessageKey.SHARED_ERROR_UNEXPECTED);
    }
}
