package com.github.mohrezal.springbootblogrestapi.domains.users.exceptions;

import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserEmailAlreadyExistsException;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserInvalidCredentialsException;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class UserExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleEmailAlreadyExistsException(
            UserEmailAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserInvalidCredentialsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleInvalidCredentialsException(
            UserInvalidCredentialsException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserInvalidRefreshTokenException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserInvalidRefreshTokenException(
            UserInvalidRefreshTokenException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }
}
