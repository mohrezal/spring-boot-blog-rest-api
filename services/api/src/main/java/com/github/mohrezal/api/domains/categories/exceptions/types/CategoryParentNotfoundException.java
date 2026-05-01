package com.github.mohrezal.api.domains.categories.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;

public class CategoryParentNotfoundException extends ResourceNotFoundException {
    public CategoryParentNotfoundException() {
        super(MessageKey.CATEGORIES_PARENT_NOT_FOUND);
    }
}
