package com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;

public class PostSlugAlreadyExistsException extends ResourceConflictException {
    public PostSlugAlreadyExistsException() {
        super(MessageKey.POST_SLUG_ALREADY_EXISTS);
    }
}
