package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;

public class UserHandleAlreadyExistsException extends ResourceConflictException {
    public UserHandleAlreadyExistsException() {
        super(MessageKey.USER_HANDLE_ALREADY_EXISTS);
    }
}
