package com.github.mohrezal.springbootblogrestapi.domains.storage.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageFileResponse {
    private byte[] data;
    private String mimeType;
    private String filename;
}
