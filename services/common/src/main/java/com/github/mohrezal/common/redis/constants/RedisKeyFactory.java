package com.github.mohrezal.common.redis.constants;

public final class RedisKeyFactory {
    private RedisKeyFactory() {}

    private static String build(String... segments) {
        return String.join(":", segments);
    }

    public static final class Verification {
        private static final String PREFIX = "verify:email";

        public static String token(String token) {
            return build(PREFIX, token);
        }

        public static final long TTL_SECONDS = 86_400;
    }

    public static final class Notification {
        private static final String PREFIX = "notification";
        private static final String DELIVERED = "delivered";

        public static String delivered(String token) {
            return build(PREFIX, DELIVERED, token);
        }

        public static final long TTL_SECONDS = 86_400;
    }
}
