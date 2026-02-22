package com.github.mohrezal.api.domains.redirects.exceptions;

import com.github.mohrezal.api.domains.redirects.exceptions.types.RedirectNotFoundException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RedirectExceptionHandler extends AbstractExceptionHandler {

    protected RedirectExceptionHandler(MessageSource messageSource) {
        super(messageSource);
    }

    @ExceptionHandler(RedirectNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleRedirectNotFoundException(
            RedirectNotFoundException ex) {
        return buildErrorResponse(ex);
    }
}
