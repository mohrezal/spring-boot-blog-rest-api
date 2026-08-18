package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserEmailFormatException extends InvalidRequestException {
    public UserEmailFormatException() {
        super(MessageKey.Shared.Validation.EMAIL);
    }
}
