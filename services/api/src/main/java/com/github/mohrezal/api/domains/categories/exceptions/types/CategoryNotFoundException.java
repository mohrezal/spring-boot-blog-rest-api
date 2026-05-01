package com.github.mohrezal.api.domains.categories.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException() {
        super(MessageKey.CATEGORIES_NOT_FOUND);
    }

    public CategoryNotFoundException(ExceptionContext context) {
        super(MessageKey.CATEGORIES_NOT_FOUND, context);
    }

    public CategoryNotFoundException(ExceptionContext context, Throwable cause) {
        super(MessageKey.CATEGORIES_NOT_FOUND, context, cause);
    }
}
