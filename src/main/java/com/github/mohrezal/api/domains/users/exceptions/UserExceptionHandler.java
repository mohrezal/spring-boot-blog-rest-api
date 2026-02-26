package com.github.mohrezal.api.domains.users.exceptions;

import com.github.mohrezal.api.domains.users.exceptions.types.UserAlreadyFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserCannotFollowOrUnfollowSelfException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailAlreadyExistsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidCredentialsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserRefreshTokenNotFoundException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class UserExceptionHandler extends AbstractExceptionHandler {

    public UserExceptionHandler(MessageSource messageSource, CookieUtils cookieUtils) {
        super(messageSource, cookieUtils);
    }

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
        return buildErrorResponseAndClearAuthCookies(ex);
    }

    @ExceptionHandler(UserRefreshTokenNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserRefreshTokenNotFoundException(
            UserRefreshTokenNotFoundException ex, WebRequest request) {
        return buildErrorResponseAndClearAuthCookies(ex);
    }

    @ExceptionHandler(UserAlreadyFollowingException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserAlreadyFollowingException(
            UserAlreadyFollowingException ex) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserCannotFollowOrUnfollowSelfException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserCannotFollowSelfException(
            UserCannotFollowOrUnfollowSelfException ex) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserNotFollowingException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserNotFollowingException(
            UserNotFollowingException ex) {
        return buildErrorResponse(ex);
    }
}
