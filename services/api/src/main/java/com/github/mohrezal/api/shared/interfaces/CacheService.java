package com.github.mohrezal.api.shared.interfaces;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {
    record CounterState(long value, long ttlSeconds) {}

    <T> Optional<T> get(String key, Class<T> type);

    void set(String key, Object value, Duration ttl);

    void delete(String key);

    Optional<CounterState> increment(String key, Duration window);
}
