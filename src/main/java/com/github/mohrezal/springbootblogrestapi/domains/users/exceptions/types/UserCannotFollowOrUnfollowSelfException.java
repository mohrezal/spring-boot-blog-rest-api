package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;

public class UserCannotFollowOrUnfollowSelfException extends InvalidRequestException {
    public UserCannotFollowOrUnfollowSelfException() {
        super(MessageKey.USER_CANNOT_FOLLOW_OR_UNFOLLOW_SELF);
    }
}
