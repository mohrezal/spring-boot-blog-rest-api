package com.github.mohrezal.api.shared.exceptions;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.api.shared.exceptions.types.InternalException;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.api.shared.exceptions.types.SlugGenerationException;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;
import com.github.mohrezal.api.shared.exceptions.types.UnexpectedException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @ExceptionHandler(SlugGenerationException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleSlugGenerationException(
            SlugGenerationException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(MessageKey.SHARED_ERROR_BAD_CREDENTIALS.getMessage())
                        .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<@NonNull ErrorResponse> handleSpringSecurityAccessDeniedException(
            Exception ex, WebRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            ErrorResponse errorResponse =
                    ErrorResponse.builder()
                            .message(MessageKey.SHARED_ERROR_UNAUTHORIZED.getMessage())
                            .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(MessageKey.SHARED_ERROR_FORBIDDEN.getMessage())
                        .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(MessageKey.SHARED_ERROR_UNEXPECTED.getMessage())
                        .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
