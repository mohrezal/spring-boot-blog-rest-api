package com.github.mohrezal.springbootblogrestapi.shared.constants;

public final class RegexUtils {
    private RegexUtils() {}

    public static final String NAME_PATTERN = "^[\\p{L} '-]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$";
}
