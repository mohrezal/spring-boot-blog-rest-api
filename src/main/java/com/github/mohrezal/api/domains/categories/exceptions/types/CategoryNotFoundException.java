package com.github.mohrezal.api.domains.categories.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException() {
        super(MessageKey.CATEGORIES_NOT_FOUND);
    }
}
