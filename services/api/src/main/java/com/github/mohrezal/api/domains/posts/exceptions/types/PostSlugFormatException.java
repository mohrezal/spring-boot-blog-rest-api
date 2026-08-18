package com.github.mohrezal.api.domains.posts.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.common.constants.MessageKey;

public class PostSlugFormatException extends InvalidRequestException {
    public PostSlugFormatException() {
        super(MessageKey.Post.Error.SLUG_INVALID_FORMAT);
    }
}
