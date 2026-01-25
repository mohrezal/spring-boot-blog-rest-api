package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;

public class UserHandleReservedException extends ResourceConflictException {
    public UserHandleReservedException() {
        super(MessageKey.USER_HANDLE_RESERVED);
    }
}
