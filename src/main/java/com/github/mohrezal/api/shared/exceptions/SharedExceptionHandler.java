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
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Slf4j
public class SharedExceptionHandler extends AbstractExceptionHandler {

    public SharedExceptionHandler(MessageSource messageSource) {
        super(messageSource);
    }

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
                        .message(resolveMessage(MessageKey.SHARED_ERROR_BAD_CREDENTIALS))
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
                            .message(resolveMessage(MessageKey.SHARED_ERROR_UNAUTHORIZED))
                            .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.SHARED_ERROR_FORBIDDEN))
                        .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.SHARED_VALIDATION_FAILED))
                        .errors(errors)
                        .build();
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getParameterValidationResults()
                .forEach(
                        result -> {
                            String parameterName = result.getMethodParameter().getParameterName();
                            result.getResolvableErrors()
                                    .forEach(
                                            error -> {
                                                errors.put(
                                                        parameterName, error.getDefaultMessage());
                                            });
                        });

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.SHARED_VALIDATION_FAILED))
                        .errors(errors)
                        .build();

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.SHARED_ERROR_UNEXPECTED))
                        .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
