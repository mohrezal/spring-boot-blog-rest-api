package com.github.mohrezal.api.shared.exceptions.types;

import com.github.mohrezal.common.constants.MessageKey;

public class SlugGenerationException extends InternalException {
    public SlugGenerationException() {
        super(MessageKey.Shared.Error.SLUG_GENERATION_FAILED);
    }
}
