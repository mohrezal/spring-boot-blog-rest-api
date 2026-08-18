package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class UserInvalidVerificationTokenException extends InvalidRequestException {
    public UserInvalidVerificationTokenException() {
        super(MessageKey.USER_INVALID_VERIFICATION_TOKEN);
    }
}
