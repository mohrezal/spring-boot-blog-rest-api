package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserHandleFormatException extends InvalidRequestException {
    public UserHandleFormatException() {
        super(MessageKey.User.Validation.HANDLE_PATTERN);
    }
}
