package com.github.mohrezal.api.domains.redirects.services.store;

import com.github.mohrezal.api.domains.redirects.dtos.RedirectCache;
import com.github.mohrezal.api.domains.redirects.enums.RedirectCacheKey;
import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.models.Redirect;
import com.github.mohrezal.api.domains.redirects.repositories.RedirectRepository;
import com.github.mohrezal.api.shared.abstracts.AbstractStore;
import com.github.mohrezal.api.shared.events.cache.CacheDeleteEvent;
import com.github.mohrezal.api.shared.events.cache.CacheSetEvent;
import com.github.mohrezal.api.shared.interfaces.CacheService;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class RedirectStoreServiceImpl extends AbstractStore<Redirect, RedirectCache>
        implements RedirectStoreService {

    private final RedirectRepository redirectRepository;

    public RedirectStoreServiceImpl(
            RedirectRepository repository,
            CacheService cacheService,
            ApplicationEventPublisher eventPublisher) {
        super(repository, Duration.ofMinutes(60), cacheService, eventPublisher);
        this.redirectRepository = repository;
    }

    @Override
    public Optional<RedirectCache> findByCode(String code) {
        var key = buildKey(RedirectCacheKey.BY_CODE, code);

        var cached = cacheService.get(key, RedirectCache.class);
        if (cached.isPresent()) {
            return cached;
        }

        var found = redirectRepository.findByCode(code);
        found.ifPresent(
                redirect ->
                        eventPublisher.publishEvent(
                                new CacheSetEvent(key, toCacheDto(redirect), ttl)));
        found.ifPresent(
                redirect ->
                        eventPublisher.publishEvent(
                                new CacheSetEvent(
                                        buildTargetPointerKey(
                                                redirect.getTargetType(), redirect.getTargetId()),
                                        redirect.getCode(),
                                        ttl)));
        return found.map(this::toCacheDto);
    }

    @Override
    public Optional<String> findCodeByTargetTypeAndTargetId(
            RedirectTargetType type, UUID targetId) {
        var pointerKey = buildTargetPointerKey(type, targetId);

        var cachedCode = cacheService.get(pointerKey, String.class);
        if (cachedCode.isPresent()) {
            return cachedCode;
        }

        var found = redirectRepository.findByTargetTypeAndTargetId(type, targetId);
        found.ifPresent(
                redirect -> {
                    eventPublisher.publishEvent(
                            new CacheSetEvent(pointerKey, redirect.getCode(), ttl));
                    eventPublisher.publishEvent(
                            new CacheSetEvent(
                                    buildKey(RedirectCacheKey.BY_CODE, redirect.getCode()),
                                    toCacheDto(redirect),
                                    ttl));
                });
        return found.map(Redirect::getCode);
    }

    @Override
    public void deleteByTargetTypeAndTargetId(RedirectTargetType type, UUID targetId) {
        redirectRepository
                .findByTargetTypeAndTargetId(type, targetId)
                .ifPresent(
                        redirect -> {
                            delete(redirect, RedirectCacheKey.BY_CODE, redirect.getCode());
                            eventPublisher.publishEvent(
                                    new CacheDeleteEvent(
                                            buildTargetPointerKey(
                                                    redirect.getTargetType(),
                                                    redirect.getTargetId())));
                        });
    }

    private String buildTargetPointerKey(RedirectTargetType type, UUID targetId) {
        return buildKey(RedirectCacheKey.BY_TARGET_TYPE_AND_ID, type.name() + ":" + targetId);
    }

    @Override
    protected RedirectCache toCacheDto(Redirect entity) {
        return new RedirectCache(entity.getTargetType(), entity.getTargetId());
    }
}
