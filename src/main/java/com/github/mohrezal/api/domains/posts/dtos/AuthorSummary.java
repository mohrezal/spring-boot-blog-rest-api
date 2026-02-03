package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorSummary {
    private UUID id;
    private String handle;
    private String firstName;
    private String lastName;
    private StorageSummary avatar;
}
