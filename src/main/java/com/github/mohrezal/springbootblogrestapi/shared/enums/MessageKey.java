package com.github.mohrezal.springbootblogrestapi.shared.enums;

import lombok.Getter;

@Getter
public enum MessageKey {
    SHARED_ERROR_ACCESS_DENIED(
            "Access denied. You do not have permission to access this resource."),
    SHARED_ERROR_UNAUTHORIZED("Authentication required."),
    SHARED_ERROR_FORBIDDEN("Forbidden. You do not have the necessary permissions."),
    SHARED_ERROR_RESOURCE_NOT_FOUND("The requested resource was not found."),
    SHARED_ERROR_RESOURCE_CONFLICT("Resource conflict."),
    SHARED_ERROR_INVALID_REQUEST("Invalid request."),
    SHARED_ERROR_INTERNAL("An internal server error occurred."),
    SHARED_ERROR_UNEXPECTED("An unexpected error occurred."),
    SHARED_ERROR_BAD_CREDENTIALS("Invalid email or password."),
    SHARED_ERROR_SLUG_GENERATION_FAILED("Slug generation failed"),

    USER_NOT_FOUND("User not found."),
    USER_EMAIL_ALREADY_EXISTS("A user account with this email address already exists."),
    USER_INVALID_CREDENTIALS("Invalid email or password."),
    USER_ERROR_EMAIL_CONFLICT("A user account with this email address already exists."),
    USER_INVALID_REFRESH_TOKEN("Invalid or expired refresh token."),
    USER_REFRESH_TOKEN_NOT_FOUND("Refresh token not found."),

    POST_NOT_FOUND("Post not found."),
    POST_SLUG_ALREADY_EXISTS("Slug already exists."),
    POST_SLUG_INVALID_FORMAT("Invalid slug format."),
    POST_STATUS_TRANSITION_INVALID("Cannot transition post to this status."),

    CATEGORIES_NOT_FOUND("One or more categories not found."),

    STORAGE_FILE_SIZE_EXCEEDED("File size exceeds the maximum allowed size."),
    STORAGE_FILE_EXTENSION_NOT_ALLOWED("File extension is not allowed.");

    private final String message;

    MessageKey(String message) {
        this.message = message;
    }
}
