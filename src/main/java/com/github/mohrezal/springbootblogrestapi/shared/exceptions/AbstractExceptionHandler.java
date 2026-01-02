package com.github.mohrezal.springbootblogrestapi.shared.exceptions;

import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.BaseException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;

public abstract class AbstractExceptionHandler {

    public ResponseEntity<@NonNull ErrorResponse> buildErrorResponse(BaseException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ErrorResponse.builder().message(ex.getMessage()).build());
    }
}
