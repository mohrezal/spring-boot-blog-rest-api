package com.github.mohrezal.api.shared.services.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mohrezal.api.shared.enums.RateLimitRedisKey;
import com.github.mohrezal.api.shared.interfaces.CacheService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {

    private static final Duration RATE_LIMIT_WINDOW = Duration.ofMinutes(1);

    private final CacheService cacheService;
    private final Cache<String, Bucket> buckets =
            Caffeine.newBuilder().expireAfterAccess(Duration.ofHours(1)).build();

    @Override
    public ConsumptionResult tryConsume(String key, Integer limitPerMinute) {
        String redisKey = RateLimitRedisKey.MAIN.build(key);
        return cacheService
                .increment(redisKey, RATE_LIMIT_WINDOW)
                .map(
                        counterState -> {
                            boolean allowed = counterState.value() <= limitPerMinute;
                            long remaining =
                                    allowed
                                            ? Math.max(0L, limitPerMinute - counterState.value())
                                            : 0L;
                            return new ConsumptionResult(
                                    allowed, remaining, counterState.ttlSeconds());
                        })
                .orElseGet(
                        () -> {
                            log.warn(
                                    "Redis rate limit unavailable for key='{}'. Falling back to"
                                            + " local bucket.",
                                    key);
                            return tryConsumeFallback(key, limitPerMinute);
                        });
    }

    private ConsumptionResult tryConsumeFallback(String key, Integer limitPerMinute) {
        Bucket bucket =
                buckets.get(
                        RateLimitRedisKey.FALLBACK.build(key),
                        ignored -> createBucket(limitPerMinute));
        if (bucket == null) {
            return new ConsumptionResult(true, limitPerMinute, 0);
        }
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        return new ConsumptionResult(
                probe.isConsumed(),
                probe.getRemainingTokens(),
                probe.getNanosToWaitForRefill() / 1_000_000_000);
    }

    private Bucket createBucket(Integer limitPerMinute) {
        Bandwidth bandwidth =
                Bandwidth.builder()
                        .capacity(limitPerMinute)
                        .refillGreedy(limitPerMinute, Duration.ofMinutes(1))
                        .build();
        return Bucket.builder().addLimit(bandwidth).build();
    }
}
