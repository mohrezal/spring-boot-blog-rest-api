package com.github.mohrezal.api.domains.categories.exceptions.types;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.common.constants.MessageKey;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException() {
        super(MessageKey.Category.Error.NOT_FOUND);
    }

    public CategoryNotFoundException(ExceptionContext context) {
        super(MessageKey.Category.Error.NOT_FOUND, context);
    }

    public CategoryNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.Category.Error.NOT_FOUND, context, cause);
    }
}
