package com.github.mohrezal.api.domains.posts.exceptions;

import com.github.mohrezal.api.domains.posts.exceptions.types.PostInvalidStatusTransitionException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugAlreadyExistsException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugFormatException;
import com.github.mohrezal.api.shared.exceptions.AbstractExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.ErrorResponse;
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

    @ExceptionHandler(PostSlugFormatException.class)
    public ResponseEntity<@NonNull ErrorResponse> handlePostSlugFormatException(
            PostSlugFormatException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(PostSlugAlreadyExistsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handlePostSlugAlreadyExistsException(
            PostSlugAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(PostInvalidStatusTransitionException.class)
    public ResponseEntity<@NonNull ErrorResponse> handlePostInvalidStatusTransitionException(
            PostInvalidStatusTransitionException ex, WebRequest request) {
        return buildErrorResponse(ex);
    }
}
