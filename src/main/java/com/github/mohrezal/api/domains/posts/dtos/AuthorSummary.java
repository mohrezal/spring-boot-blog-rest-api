package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import java.util.UUID;

public record AuthorSummary(
        UUID id, String handle, String firstName, String lastName, StorageSummary avatar) {}
