package com.github.mohrezal.api.domains.redirects.enums;

import com.github.mohrezal.api.shared.interfaces.CacheKeyPrefix;

public enum RedirectCacheKey implements CacheKeyPrefix {
    BY_CODE("redirects:code:"),
    BY_TARGET_TYPE_AND_ID("redirects:target:");

    private final String prefix;

    RedirectCacheKey(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}
