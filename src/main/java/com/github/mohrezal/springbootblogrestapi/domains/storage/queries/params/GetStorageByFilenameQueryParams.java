package com.github.mohrezal.springbootblogrestapi.domains.storage.queries.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetStorageByFilenameQueryParams {
    private String filename;
}
