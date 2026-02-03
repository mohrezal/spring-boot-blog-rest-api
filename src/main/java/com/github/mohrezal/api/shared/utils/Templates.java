package com.github.mohrezal.api.shared.utils;

public final class Templates {
    private Templates() {}

    public static final class Notification {
        private static final String BASE = "notification/";
    }

    public static final class Email {
        private static final String BASE = "email/";
        public static final String WELCOME = BASE + "welcome";
    }
}
