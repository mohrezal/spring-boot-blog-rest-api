package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;

public class UserNotFollowingException extends InvalidRequestException {
    public UserNotFollowingException() {
        super(MessageKey.USER_NOT_FOLLOWING);
    }
}
