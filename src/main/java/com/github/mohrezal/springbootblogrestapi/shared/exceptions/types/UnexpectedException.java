package com.github.mohrezal.springbootblogrestapi.shared.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;

public class UnexpectedException extends InternalException {
    public UnexpectedException() {
        super(MessageKey.SHARED_ERROR_UNEXPECTED);
    }
}
