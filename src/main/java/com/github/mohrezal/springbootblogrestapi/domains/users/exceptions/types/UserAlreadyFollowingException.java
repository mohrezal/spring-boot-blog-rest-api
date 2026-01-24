package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;

public class UserAlreadyFollowingException extends InvalidRequestException {
    public UserAlreadyFollowingException() {
        super(MessageKey.USER_ALREADY_FOLLOWING);
    }
}
