package com.github.mohrezal.api.shared.abstracts;

import com.github.mohrezal.api.shared.events.cache.CacheDeleteEvent;
import com.github.mohrezal.api.shared.events.cache.CacheSetEvent;
import com.github.mohrezal.api.shared.interfaces.CacheKeyPrefix;
import com.github.mohrezal.api.shared.interfaces.CacheService;
import com.github.mohrezal.api.shared.interfaces.StoreService;
import com.github.mohrezal.api.shared.models.BaseModel;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
public abstract class AbstractStore<E extends BaseModel, C> implements StoreService<E> {
    protected final JpaRepository<E, UUID> repository;
    protected final Duration ttl;
    protected final CacheService cacheService;
    protected final ApplicationEventPublisher eventPublisher;

    protected abstract C toCacheDto(E entity);

    protected String buildKey(CacheKeyPrefix prefix, String identifier) {
        return prefix.getPrefix() + identifier;
    }

    @Override
    public E save(E entity, CacheKeyPrefix prefix, String identifier) {
        String key = buildKey(prefix, identifier);
        var saved = repository.save(entity);
        var cacheDto = toCacheDto(saved);

        eventPublisher.publishEvent(new CacheSetEvent(key, cacheDto, ttl));
        return saved;
    }

    @Override
    public void delete(E entity, CacheKeyPrefix prefix, String identifier) {
        String key = buildKey(prefix, identifier);
        repository.delete(entity);
        eventPublisher.publishEvent(new CacheDeleteEvent(key));
    }
}
