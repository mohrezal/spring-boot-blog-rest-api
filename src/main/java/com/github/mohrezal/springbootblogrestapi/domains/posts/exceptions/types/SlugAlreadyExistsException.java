package com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;

public class SlugAlreadyExistsException extends ResourceConflictException {
    public SlugAlreadyExistsException() {
        super(MessageKey.POST_SLUG_ALREADY_EXISTS);
    }
}
