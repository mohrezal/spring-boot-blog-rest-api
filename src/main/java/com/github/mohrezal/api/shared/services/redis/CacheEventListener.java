package com.github.mohrezal.api.shared.services.redis;

import com.github.mohrezal.api.shared.events.cache.CacheDeleteEvent;
import com.github.mohrezal.api.shared.events.cache.CacheSetEvent;
import com.github.mohrezal.api.shared.interfaces.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheEventListener {

    private final CacheService cacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleCacheSetEvent(CacheSetEvent event) {
        try {
            cacheService.set(event.key(), event.value(), event.ttl());
        } catch (Exception e) {
            log.error("Cache SET failed for key '{}': {}", event.key(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleCacheDeleteEvent(CacheDeleteEvent event) {
        try {
            cacheService.delete(event.key());
        } catch (Exception e) {
            log.error("Cache DELETE failed for key '{}': {}", event.key(), e.getMessage());
        }
    }
}
