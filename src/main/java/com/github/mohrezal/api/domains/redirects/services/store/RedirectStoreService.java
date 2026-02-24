package com.github.mohrezal.api.domains.redirects.services.store;

import com.github.mohrezal.api.domains.redirects.dtos.RedirectCache;
import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.models.Redirect;
import com.github.mohrezal.api.shared.interfaces.StoreService;
import java.util.Optional;
import java.util.UUID;

public interface RedirectStoreService extends StoreService<Redirect> {

    Optional<RedirectCache> findByCode(String code);

    Optional<String> findCodeByTargetTypeAndTargetId(RedirectTargetType type, UUID targetId);

    void deleteByTargetTypeAndTargetId(RedirectTargetType type, UUID targetId);
}
