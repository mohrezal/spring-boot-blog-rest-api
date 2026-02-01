package com.github.mohrezal.springbootblogrestapi.domains.notifications.exceptions;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.exceptions.types.NotificationNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.exceptions.types.NotificationPreferencesNotFoundException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class NotificationExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserNotFoundException(
            NotificationNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(NotificationPreferencesNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleNotificationPreferencesNotFoundException(
            NotificationPreferencesNotFoundException ex) {
        return buildErrorResponse(ex);
    }
}
