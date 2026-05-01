package com.github.mohrezal.api.shared.exceptions;

import com.github.mohrezal.api.shared.exceptions.types.BaseException;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class AbstractExceptionHandler {

    protected final MessageSource messageSource;
    private final CookieUtils cookieUtils;

    protected AbstractExceptionHandler(MessageSource messageSource, CookieUtils cookieUtils) {
        this.messageSource = messageSource;
        this.cookieUtils = cookieUtils;
    }

    public ResponseEntity<@NonNull ErrorResponse> buildErrorResponse(BaseException ex) {
        if (ex.getStatusCode().is5xxServerError()) {
            log.error(
                    "Handled exception - messageKey={} status={} context={}",
                    ex.getMessageKey(),
                    ex.getStatusCode().value(),
                    ex.getContext(),
                    ex);
        } else {
            log.warn(
                    "Handled exception - messageKey={} status={} context={}",
                    ex.getMessageKey(),
                    ex.getStatusCode().value(),
                    ex.getContext());
        }

        String message =
                messageSource.getMessage(ex.getMessageKey(), null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ErrorResponse.builder().message(message).build());
    }

    public ResponseEntity<@NonNull ErrorResponse> buildErrorResponseAndClearAuthCookies(
            BaseException ex) {
        ResponseEntity<@NonNull ErrorResponse> errorResponse = buildErrorResponse(ex);
        return ResponseEntity.status(errorResponse.getStatusCode())
                .header(HttpHeaders.SET_COOKIE, cookieUtils.deleteAccessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookieUtils.deleteRefreshTokenCookie().toString())
                .body(errorResponse.getBody());
    }

    protected String resolveMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
