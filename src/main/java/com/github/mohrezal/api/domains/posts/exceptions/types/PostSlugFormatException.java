package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;

public class PostSlugFormatException extends InvalidRequestException {
    public PostSlugFormatException() {
        super(MessageKey.POST_SLUG_INVALID_FORMAT);
    }
}
