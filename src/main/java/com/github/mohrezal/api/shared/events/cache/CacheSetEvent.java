package com.github.mohrezal.api.shared.events.cache;

import java.time.Duration;

public record CacheSetEvent(String key, Object value, Duration ttl) {}
