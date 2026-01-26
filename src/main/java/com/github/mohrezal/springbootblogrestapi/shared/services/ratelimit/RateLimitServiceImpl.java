package com.github.mohrezal.springbootblogrestapi.shared.services.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.UnexpectedException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final Cache<String, Bucket> buckets =
            Caffeine.newBuilder().expireAfterAccess(Duration.ofHours(1)).build();

    @Override
    public ConsumptionResult tryConsume(String key, Integer limitPerMinute) {
        Bucket bucket = buckets.get(key, k -> createBucket(limitPerMinute));
        if (bucket == null) {
            throw new UnexpectedException();
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
