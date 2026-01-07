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

    public static final class User {
        public static final String BASE = API_BASE + "users";
        public static final String ME = "me";
    }

    public static final class Category {
        public static final String BASE = API_BASE + "categories";
    }

    public static final class Post {
        public static final String BASE = API_BASE + "posts";
        public static final String GET_POST_BY_SLUG = "{slug}";
    }
}
