package com.github.mohrezal.springbootblogrestapi.domains.storage.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResponse {
    private UUID id;
    private String filename;
    private String originalFilename;
    private String mimeType;
    private Long size;
    private String title;
    private String alt;
    private OffsetDateTime createdAt;
}
