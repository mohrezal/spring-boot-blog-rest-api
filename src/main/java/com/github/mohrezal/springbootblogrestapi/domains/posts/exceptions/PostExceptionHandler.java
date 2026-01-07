package com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions;

import com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class PostExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handlePostNotFoundException(
            PostNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }
}
