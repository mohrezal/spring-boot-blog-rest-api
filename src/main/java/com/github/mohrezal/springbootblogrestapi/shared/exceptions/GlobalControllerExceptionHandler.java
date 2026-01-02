package com.github.mohrezal.springbootblogrestapi.shared.exceptions;

import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalControllerExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }
}
