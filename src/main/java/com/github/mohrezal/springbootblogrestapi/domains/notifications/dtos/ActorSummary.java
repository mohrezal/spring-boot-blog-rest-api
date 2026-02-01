package com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos;

import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageSummary;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActorSummary {
    private UUID id;
    private String handle;
    private String firstName;
    private String lastName;
    private StorageSummary avatar;
}
