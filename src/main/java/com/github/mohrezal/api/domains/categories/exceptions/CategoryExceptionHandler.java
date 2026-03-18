package com.github.mohrezal.api.domains.categories.exceptions;

import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryParentNotfoundException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CategoryExceptionHandler extends AbstractExceptionHandler {

    public CategoryExceptionHandler(MessageSource messageSource, CookieUtils cookieUtils) {
        super(messageSource, cookieUtils);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(
            CategoryNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(CategoryParentNotfoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryParentNotfoundException(
            CategoryParentNotfoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }
}
