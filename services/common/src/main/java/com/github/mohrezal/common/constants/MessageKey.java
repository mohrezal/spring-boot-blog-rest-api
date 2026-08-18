package com.github.mohrezal.common.constants;

public final class MessageKey {

    private MessageKey() {}

    public static final class Shared {
        public static final class Error {
            public static final String ACCESS_DENIED = "shared.error.access-denied";
            public static final String UNAUTHORIZED = "shared.error.unauthorized";
            public static final String FORBIDDEN = "shared.error.forbidden";
            public static final String RESOURCE_NOT_FOUND = "shared.error.resource-not-found";
            public static final String RESOURCE_CONFLICT = "shared.error.resource-conflict";
            public static final String INVALID_REQUEST = "shared.error.invalid-request";
            public static final String INTERNAL = "shared.error.internal";
            public static final String UNEXPECTED = "shared.error.unexpected";
            public static final String BAD_CREDENTIALS = "shared.error.bad-credentials";
            public static final String SLUG_GENERATION_FAILED =
                    "shared.error.slug-generation-failed";
            public static final String RATE_LIMIT_EXCEEDED = "shared.error.rate-limit-exceeded";
            public static final String INVALID_REDIRECT_URL = "shared.error.invalid-redirect-url";
        }

        public static final class Validation {
            public static final String FAILED = "shared.validation.failed";
            public static final String EMAIL = "shared.validation.email";
            public static final String NOT_BLANK = "{shared.validation.not-blank}";
            public static final String NOT_NULL = "{shared.validation.not-null}";
            public static final String NOT_EMPTY = "{shared.validation.not-empty}";
            public static final String EMAIL_KEY = "{shared.validation.email}";
            public static final String SIZE = "{shared.validation.size}";
            public static final String SIZE_MAX = "{shared.validation.size-max}";
            public static final String RANGE = "{shared.validation.range}";
            public static final String RANGE_MIN = "{shared.validation.range.min}";
            public static final String RANGE_MAX = "{shared.validation.range.max}";
        }
    }

    public static final class User {
        public static final class Error {
            public static final String NOT_FOUND = "user.error.not-found";
            public static final String EMAIL_ALREADY_EXISTS = "user.error.email-already-exists";
            public static final String INVALID_CREDENTIALS = "user.error.invalid-credentials";
            public static final String EMAIL_CONFLICT = "user.error.email-conflict";
            public static final String INVALID_REFRESH_TOKEN = "user.error.invalid-refresh-token";
            public static final String REFRESH_TOKEN_NOT_FOUND =
                    "user.error.refresh-token-not-found";
            public static final String CANNOT_FOLLOW_OR_UNFOLLOW_SELF =
                    "user.error.cannot-follow-or-unfollow-self";
            public static final String ALREADY_FOLLOWING = "user.error.already-following";
            public static final String NOT_FOLLOWING = "user.error.not-following";
            public static final String HANDLE_ALREADY_EXISTS = "user.error.handle-already-exists";
            public static final String HANDLE_RESERVED = "user.error.handle-reserved";
            public static final String EMAIL_UNAVAILABLE = "user.error.email-unavailable";
            public static final String HANDLE_UNAVAILABLE = "user.error.handle-unavailable";
            public static final String INVALID_VERIFICATION_TOKEN =
                    "user.error.invalid-verification-token";
            public static final String ALREADY_VERIFIED = "user.error.already-verified";
            public static final String EMAIL_NOT_VERIFIED = "user.error.email-not-verified";
        }

        public static final class Validation {
            public static final String HANDLE_PATTERN = "user.validation.handle-pattern";
            public static final String NAME_PATTERN = "{user.validation.name-pattern}";
            public static final String HANDLE_PATTERN_KEY = "{user.validation.handle-pattern}";
            public static final String PASSWORD_PATTERN = "{user.validation.password-pattern}";
        }
    }

    public static final class Post {
        public static final class Error {
            public static final String NOT_FOUND = "post.error.not-found";
            public static final String SLUG_ALREADY_EXISTS = "post.error.slug-already-exists";
            public static final String SLUG_INVALID_FORMAT = "post.error.slug-invalid-format";
            public static final String STATUS_TRANSITION_INVALID =
                    "post.error.status-transition-invalid";
            public static final String SLUG_INVALID_FORMAT_KEY = "{post.error.slug-invalid-format}";
        }
    }

    public static final class Category {
        public static final class Error {
            public static final String NOT_FOUND = "categories.error.not-found";
            public static final String PARENT_NOT_FOUND = "categories.error.parent.not-found";
        }
    }

    public static final class Storage {
        public static final class Error {
            public static final String FILE_SIZE_EXCEEDED = "storage.error.file-size-exceeded";
            public static final String FILE_MIME_TYPE_NOT_ALLOWED =
                    "storage.error.file-mime-type-not-allowed";
            public static final String UPLOAD_FAILED = "storage.error.upload-failed";
        }
    }

    public static final class Notification {
        public static final class Error {
            public static final String NOT_FOUND = "notification.error.not-found";
            public static final String PREFERENCE_NOT_FOUND =
                    "notification.error.preference-not-found";
        }
    }

    public static final class Privilege {
        public static final class Error {
            public static final String PERMISSION_NOT_FOUND =
                    "privilege.error.permission-not-found";
            public static final String PROTECTED_PERMISSION_CANNOT_BE_DISABLED =
                    "privilege.error.protected-permission-cannot-be-disabled";
            public static final String ROLE_NOT_FOUND = "privilege.error.role-not-found";
            public static final String ROLE_KEY_ALREADY_EXISTS =
                    "privilege.error.role-key-already-exists";
            public static final String CONFIGURED_ROLE_CANNOT_BE_DELETED =
                    "privilege.error.configured-role-cannot-be-deleted";
            public static final String OWNER_ROLE_CANNOT_BE_UPDATED =
                    "privilege.error.owner-role-cannot-be-updated";
            public static final String LAST_OWNER_ROLE_CANNOT_BE_REMOVED =
                    "privilege.error.last-owner-role-cannot-be-removed";
            public static final String ROLE_ASSIGNED_TO_USERS =
                    "privilege.error.role-assigned-to-users";
        }
    }
}
