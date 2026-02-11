package com.github.mohrezal.api.shared.enums;

public final class MessageKey {

    private MessageKey() {}

    public static final String SHARED_ERROR_ACCESS_DENIED = "shared.error.access-denied";
    public static final String SHARED_ERROR_UNAUTHORIZED = "shared.error.unauthorized";
    public static final String SHARED_ERROR_FORBIDDEN = "shared.error.forbidden";
    public static final String SHARED_ERROR_RESOURCE_NOT_FOUND = "shared.error.resource-not-found";
    public static final String SHARED_ERROR_RESOURCE_CONFLICT = "shared.error.resource-conflict";
    public static final String SHARED_ERROR_INVALID_REQUEST = "shared.error.invalid-request";
    public static final String SHARED_ERROR_INTERNAL = "shared.error.internal";
    public static final String SHARED_ERROR_UNEXPECTED = "shared.error.unexpected";
    public static final String SHARED_ERROR_BAD_CREDENTIALS = "shared.error.bad-credentials";
    public static final String SHARED_ERROR_SLUG_GENERATION_FAILED =
            "shared.error.slug-generation-failed";
    public static final String SHARED_ERROR_RATE_LIMIT_EXCEEDED =
            "shared.error.rate-limit-exceeded";

    public static final String USER_NOT_FOUND = "user.error.not-found";
    public static final String USER_EMAIL_ALREADY_EXISTS = "user.error.email-already-exists";
    public static final String USER_INVALID_CREDENTIALS = "user.error.invalid-credentials";
    public static final String USER_ERROR_EMAIL_CONFLICT = "user.error.email-conflict";
    public static final String USER_INVALID_REFRESH_TOKEN = "user.error.invalid-refresh-token";
    public static final String USER_REFRESH_TOKEN_NOT_FOUND = "user.error.refresh-token-not-found";
    public static final String USER_CANNOT_FOLLOW_OR_UNFOLLOW_SELF =
            "user.error.cannot-follow-or-unfollow-self";
    public static final String USER_ALREADY_FOLLOWING = "user.error.already-following";
    public static final String USER_NOT_FOLLOWING = "user.error.not-following";
    public static final String USER_HANDLE_ALREADY_EXISTS = "user.error.handle-already-exists";
    public static final String USER_HANDLE_RESERVED = "user.error.handle-reserved";

    public static final String POST_NOT_FOUND = "post.error.not-found";
    public static final String POST_SLUG_ALREADY_EXISTS = "post.error.slug-already-exists";
    public static final String POST_SLUG_INVALID_FORMAT = "post.error.slug-invalid-format";
    public static final String POST_STATUS_TRANSITION_INVALID =
            "post.error.status-transition-invalid";

    public static final String CATEGORIES_NOT_FOUND = "categories.error.not-found";

    public static final String STORAGE_FILE_SIZE_EXCEEDED = "storage.error.file-size-exceeded";
    public static final String STORAGE_FILE_MIME_TYPE_NOT_ALLOWED =
            "storage.error.file-mime-type-not-allowed";
    public static final String STORAGE_UPLOAD_FAILED = "storage.error.upload-failed";

    public static final String NOTIFICATION_NOT_FOUND = "notification.error.not-found";
    public static final String NOTIFICATION_PREFERENCE_NOT_FOUND =
            "notification.error.preference-not-found";

    public static final String SHARED_VALIDATION_FAILED = "shared.validation.failed";

    public static final String SHARED_NOT_BLANK = "{shared.validation.not-blank}";
    public static final String SHARED_NOT_NULL = "{shared.validation.not-null}";
    public static final String SHARED_NOT_EMPTY = "{shared.validation.not-empty}";
    public static final String SHARED_EMAIL = "{shared.validation.email}";
    public static final String SHARED_SIZE = "{shared.validation.size}";
    public static final String SHARED_VALIDATION_SIZE_MAX = "{shared.validation.size-max}";
    public static final String SHARED_VALIDATION_RANGE = "{shared.validation.range}";
    public static final String SHARED_VALIDATION_RANGE_MIN = "{shared.validation.range.min}";
    public static final String SHARED_VALIDATION_RANGE_MAX = "{shared.validation.range.max}";

    public static final String USER_NAME_PATTERN = "{user.validation.name-pattern}";
    public static final String USER_HANDLE_PATTERN = "{user.validation.handle-pattern}";
    public static final String USER_PASSWORD_PATTERN = "{user.validation.password-pattern}";

    public static final String POST_SLUG_INVALID_FORMAT_KEY = "{post.error.slug-invalid-format}";
}
