package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;

public class UserCannotFollowSelfException extends InvalidRequestException {
    public UserCannotFollowSelfException() {
        super(MessageKey.USER_CANNOT_FOLLOW_SELF);
    }
}
