package com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions;

import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StorageExceptionHandler extends AbstractExceptionHandler {

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
