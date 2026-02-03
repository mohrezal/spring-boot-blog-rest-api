package com.github.mohrezal.api.domains.categories.exceptions;

import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CategoryExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleCategoryNotFoundException(
            CategoryNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }
}
