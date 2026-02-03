package com.github.mohrezal.api.domains.users.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;

public class UserCannotFollowOrUnfollowSelfException extends InvalidRequestException {
    public UserCannotFollowOrUnfollowSelfException() {
        super(MessageKey.USER_CANNOT_FOLLOW_OR_UNFOLLOW_SELF);
    }
}
