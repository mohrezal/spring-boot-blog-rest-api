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
    SHARED_ERROR_INTERNAL("An internal server error occurred. Please try again later."),
    SHARED_ERROR_UNEXPECTED("An unexpected error occurred."),

    USER_ERROR_EMAIL_CONFLICT("A user account with this email address already exists.");

    private final String message;

    MessageKey(String message) {
        this.message = message;
    }
}
