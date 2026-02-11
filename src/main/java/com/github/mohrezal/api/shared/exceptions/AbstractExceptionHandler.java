package com.github.mohrezal.api.shared.exceptions;

import com.github.mohrezal.api.shared.exceptions.types.BaseException;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;

public abstract class AbstractExceptionHandler {

    protected final MessageSource messageSource;

    protected AbstractExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ResponseEntity<@NonNull ErrorResponse> buildErrorResponse(BaseException ex) {
        String message =
                messageSource.getMessage(ex.getMessageKey(), null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ErrorResponse.builder().message(message).build());
    }

    protected String resolveMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
