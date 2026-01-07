package com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;

public class PostNotFoundException extends ResourceNotFoundException {
    public PostNotFoundException() {
        super(MessageKey.POST_NOT_FOUND);
    }
}
