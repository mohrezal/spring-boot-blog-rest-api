package com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos;

import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageSummary;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ActorSummary(
        UUID id, String handle, String firstName, String lastName, StorageSummary avatar) {}
