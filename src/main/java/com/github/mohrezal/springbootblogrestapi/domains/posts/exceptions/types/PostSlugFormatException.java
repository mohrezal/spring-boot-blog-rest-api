package com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;

public class PostSlugFormatException extends InvalidRequestException {
    public PostSlugFormatException() {
        super(MessageKey.POST_SLUG_INVALID_FORMAT);
    }
}
