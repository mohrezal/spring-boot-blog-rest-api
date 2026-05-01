package com.github.mohrezal.api.domains.storage.exceptions;

import com.github.mohrezal.api.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StorageExceptionHandler extends AbstractExceptionHandler {

    public StorageExceptionHandler(MessageSource messageSource, CookieUtils cookieUtils) {
        super(messageSource, cookieUtils);
    }

    @ExceptionHandler(StorageFileSizeExceededException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleStorageFileSizeExceededException(
            StorageFileSizeExceededException ex) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(StorageInvalidMimeTypeException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleStorageInvalidMimeTypeException(
            StorageInvalidMimeTypeException ex) {
        return buildErrorResponse(ex);
    }
}
