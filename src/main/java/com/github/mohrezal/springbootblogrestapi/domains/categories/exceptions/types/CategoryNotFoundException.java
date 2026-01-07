package com.github.mohrezal.springbootblogrestapi.domains.categories.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException() {
        super(MessageKey.CATEGORIES_NOT_FOUND);
    }
}
