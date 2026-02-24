package com.github.mohrezal.api.shared.interfaces;

import com.github.mohrezal.api.shared.models.BaseModel;

public interface StoreService<E extends BaseModel> {
    E save(E entity, CacheKeyPrefix prefix, String identifier);

    void delete(E entity, CacheKeyPrefix prefix, String identifier);
}
