package com.github.mohrezal.api.shared.services.redis;

import com.github.mohrezal.api.shared.interfaces.CacheService;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

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
}
