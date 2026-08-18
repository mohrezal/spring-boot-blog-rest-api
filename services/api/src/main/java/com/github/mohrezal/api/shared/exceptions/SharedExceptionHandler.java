package com.github.mohrezal.api.shared.exceptions;

import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.api.shared.exceptions.types.InternalException;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRedirectUrlException;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.api.shared.exceptions.types.SlugGenerationException;
import com.github.mohrezal.api.shared.exceptions.types.UnauthorizedException;
import com.github.mohrezal.api.shared.exceptions.types.UnexpectedException;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import com.github.mohrezal.common.constants.MessageKey;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Slf4j
public class SharedExceptionHandler extends AbstractExceptionHandler {

    public SharedExceptionHandler(MessageSource messageSource, CookieUtils cookieUtils) {
        super(messageSource, cookieUtils);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(
            ResourceConflictException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(
            InvalidRequestException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponse> handleInternalException(
            InternalException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            UnexpectedException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(SlugGenerationException.class)
    public ResponseEntity<ErrorResponse> handleSlugGenerationException(
            SlugGenerationException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        var errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.Shared.Error.BAD_CREDENTIALS))
                        .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponse> handleSpringSecurityAccessDeniedException(
            Exception ex, WebRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            var errorResponse =
                    ErrorResponse.builder()
                            .message(resolveMessage(MessageKey.Shared.Error.UNAUTHORIZED))
                            .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        var errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.Shared.Error.FORBIDDEN))
                        .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        var errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.Shared.Validation.FAILED))
                        .errors(errors)
                        .build();
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex) {
        var errors = new HashMap<String, String>();

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

        var errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.Shared.Validation.FAILED))
                        .errors(errors)
                        .build();

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(InvalidRedirectUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRedirectUrlException(
            InvalidRedirectUrlException ex) {
        var errorResponse =
                ErrorResponse.builder().message(resolveMessage(ex.getMessageKey())).build();
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        var errorResponse =
                ErrorResponse.builder()
                        .message(resolveMessage(MessageKey.Shared.Error.UNEXPECTED))
                        .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
