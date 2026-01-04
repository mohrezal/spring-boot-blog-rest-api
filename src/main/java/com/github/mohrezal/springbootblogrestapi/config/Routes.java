package com.github.mohrezal.springbootblogrestapi.config;

public final class Routes {
    private Routes() {}

    public static String build(String... segments) {
        return String.join("/", segments);
    }

    private static final String API_BASE = "/api/v1/";

    public static final class Auth {
        public static final String BASE = API_BASE + "auth";
        public static final String REGISTER = "register";
        public static final String LOGIN = "login";
        public static final String REFRESH = "refresh";
        public static final String LOGOUT = "logout";
    }
}
