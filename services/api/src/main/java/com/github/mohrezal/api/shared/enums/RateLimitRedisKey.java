package com.github.mohrezal.api.shared.enums;

public enum RateLimitRedisKey {
    MAIN("rate-limit"),
    FALLBACK("fallback-rate-limit");

    private final String prefix;

    RateLimitRedisKey(String prefix) {
        this.prefix = prefix;
    }

    public String build(String key) {
        return prefix + ":" + key;
    }
}
