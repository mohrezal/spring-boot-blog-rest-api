package com.github.mohrezal.springbootblogrestapi.shared.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;

public class SlugGenerationException extends InternalException {
    public SlugGenerationException() {
        super(MessageKey.SHARED_ERROR_SLUG_GENERATION_FAILED);
    }
}
