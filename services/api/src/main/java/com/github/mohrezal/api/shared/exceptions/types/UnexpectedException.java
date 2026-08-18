package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.common.constants.MessageKey;

public class UnexpectedException extends InternalException {
    public UnexpectedException() {
        super(MessageKey.Shared.Error.UNEXPECTED);
    }
}
