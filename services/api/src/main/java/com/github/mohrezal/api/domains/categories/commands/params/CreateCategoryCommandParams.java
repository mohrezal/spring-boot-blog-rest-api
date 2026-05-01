package com.github.mohrezal.api.domains.categories.commands.params;

import com.github.mohrezal.api.domains.categories.dtos.CreateCategoryRequest;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import org.springframework.security.core.userdetails.UserDetails;

public record CreateCategoryCommandParams(
        UserDetails userDetails, CreateCategoryRequest createCategoryRequest)
        implements AuthenticatedParams {
    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }
}
