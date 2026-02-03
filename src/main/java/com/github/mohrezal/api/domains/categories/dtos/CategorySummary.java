package com.github.mohrezal.api.domains.categories.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySummary {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
