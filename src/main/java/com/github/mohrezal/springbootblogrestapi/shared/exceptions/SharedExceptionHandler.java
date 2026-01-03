package com.github.mohrezal.springbootblogrestapi.shared.exceptions;

import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InternalException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.UnauthorizedException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.UnexpectedException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class SharedExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleForbiddenException(
            ForbiddenException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleResourceConflictException(
            ResourceConflictException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleInvalidRequestException(
            InvalidRequestException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleInternalException(
            InternalException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUnexpectedException(
            UnexpectedException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        ErrorResponse errorResponse =
                ErrorResponse.builder().message("Invalid email or password.").build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
