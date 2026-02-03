package com.github.mohrezal.api.domains.storage.queries.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetStorageByFilenameQueryParams {
    private String filename;
}
