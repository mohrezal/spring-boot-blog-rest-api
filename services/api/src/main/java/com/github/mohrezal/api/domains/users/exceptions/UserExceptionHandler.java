package com.github.mohrezal.api.domains.users.exceptions;

import com.github.mohrezal.api.domains.users.exceptions.types.UserAlreadyFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserAlreadyVerifiedException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserCannotFollowOrUnfollowSelfException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailAlreadyExistsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailNotVerifiedException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidCredentialsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidVerificationTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserRefreshTokenNotFoundException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
import com.github.mohrezal.api.shared.utils.CookieUtils;
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
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
            UserEmailAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserInvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            UserInvalidCredentialsException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserEmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserEmailNotVerifiedException(
            UserEmailNotVerifiedException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserInvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleUserInvalidRefreshTokenException(
            UserInvalidRefreshTokenException ex, WebRequest request) {
        return buildErrorResponseAndClearAuthCookies(ex);
    }

    @ExceptionHandler(UserRefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserRefreshTokenNotFoundException(
            UserRefreshTokenNotFoundException ex, WebRequest request) {
        return buildErrorResponseAndClearAuthCookies(ex);
    }

    @ExceptionHandler(UserAlreadyFollowingException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyFollowingException(
            UserAlreadyFollowingException ex) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserCannotFollowOrUnfollowSelfException.class)
    public ResponseEntity<ErrorResponse> handleUserCannotFollowSelfException(
            UserCannotFollowOrUnfollowSelfException ex) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserNotFollowingException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFollowingException(
            UserNotFollowingException ex) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserInvalidVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleUserInvalidVerificationTokenException(
            UserInvalidVerificationTokenException ex) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyVerifiedException(
            UserAlreadyVerifiedException ex) {
        return buildErrorResponse(ex);
    }
}
