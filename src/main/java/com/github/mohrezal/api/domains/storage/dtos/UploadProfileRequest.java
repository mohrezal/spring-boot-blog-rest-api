package com.github.mohrezal.api.domains.storage.dtos;

import com.github.mohrezal.api.shared.enums.MessageKey;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadProfileRequest(
        @NotNull(message = MessageKey.SHARED_NOT_NULL) MultipartFile file) {}
