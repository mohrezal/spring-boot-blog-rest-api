package com.github.mohrezal.springbootblogrestapi.shared.services.ratelimit;

public interface RateLimitService {

    record ConsumptionResult(boolean allowed, long remainingTokens, long retryAfterSeconds) {}

    ConsumptionResult tryConsume(String key, Integer limitPerMinute);
}
