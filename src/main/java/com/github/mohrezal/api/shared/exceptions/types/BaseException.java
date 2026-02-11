package com.github.mohrezal.api.shared.exceptions.types;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String messageKey;

    protected BaseException(String messageKey, HttpStatus statusCode) {
        super(messageKey);
        this.messageKey = messageKey;
        this.statusCode = statusCode;
    }
}
