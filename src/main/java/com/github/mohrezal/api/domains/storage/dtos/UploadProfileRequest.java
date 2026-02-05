package com.github.mohrezal.api.domains.storage.dtos;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadProfileRequest(@NotNull MultipartFile file) {}
