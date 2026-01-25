package com.github.mohrezal.springbootblogrestapi.shared.constants;

public final class RegexUtils {
    private RegexUtils() {}

    public static final String NAME_PATTERN = "^[\\p{L} '-]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$";
    public static final String SLUG_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";
    public static final String HANDLE_PATTERN = "^[a-z0-9_]+$";
}
