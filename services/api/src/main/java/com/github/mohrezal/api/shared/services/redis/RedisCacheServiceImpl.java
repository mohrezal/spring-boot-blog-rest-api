package com.github.mohrezal.api.shared.services.redis;

import com.github.mohrezal.api.shared.interfaces.CacheService;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Long> redisCounterTemplate;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(type.cast(value));
        } catch (Exception e) {
            log.warn("Redis GET failed for key '{}', falling back to DB: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Optional<CacheService.CounterState> increment(String key, Duration window) {
        try {
            Long currentCount = redisCounterTemplate.opsForValue().increment(key);
            if (currentCount == null) {
                log.warn("Redis INCR returned null for key='{}'.", key);
                return Optional.empty();
            }

            if (currentCount == 1L) {
                redisCounterTemplate.expire(key, window);
            }

            Long ttl = redisCounterTemplate.getExpire(key, TimeUnit.SECONDS);
            long ttlSeconds = ttl != null && ttl >= 0 ? ttl : window.toSeconds();

            return Optional.of(new CacheService.CounterState(currentCount, ttlSeconds));
        } catch (Exception e) {
            log.warn(
                    "Redis increment-in-window failed for key='{}'. Error: {}",
                    key,
                    e.getMessage());
            return Optional.empty();
        }
    }
}
