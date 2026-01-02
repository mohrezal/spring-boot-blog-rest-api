package com.github.mohrezal.springbootblogrestapi.shared.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus statusCode;
    private final MessageKey messageKey;

    public BaseException(HttpStatus statusCode, MessageKey messageKey) {
        super(messageKey.getMessage());
        this.statusCode = statusCode;
        this.messageKey = messageKey;
    }
}
