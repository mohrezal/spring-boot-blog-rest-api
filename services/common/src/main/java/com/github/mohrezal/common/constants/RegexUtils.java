package com.github.mohrezal.common.constants;

public final class RegexUtils {
    private RegexUtils() {}

    public static final String NAME_PATTERN = "^[\\p{L} '-]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$";
    public static final String SLUG_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";
    public static final String HANDLE_PATTERN = "^[a-z0-9_]+$";
    public static final String KEY_PATTERN = "^[a-z][a-z0-9._-]*$";
    public static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
}
