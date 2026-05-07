package com.github.mohrezal.common.constants;

public final class Templates {
    private Templates() {}

    public static final class Email {
        private static final String BASE = "email/";
        public static final String WELCOME = BASE + "welcome";
        public static final String VERIFICATION_REMINDER = BASE + "verification-reminder";
    }
}
