package com.github.mohrezal.api.config.security;

public final class JwtClaim {

    private JwtClaim() {}

    public static final String PERMISSIONS = "permissions";
    public static final String PRIVILEGE_VERSION = "privilegeVersion";
    public static final String TYPE = "typ";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";
}
