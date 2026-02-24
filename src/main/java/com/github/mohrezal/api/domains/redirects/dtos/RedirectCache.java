package com.github.mohrezal.api.domains.redirects.dtos;

import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import java.util.UUID;

public record RedirectCache(RedirectTargetType targetType, UUID targetId) {}
