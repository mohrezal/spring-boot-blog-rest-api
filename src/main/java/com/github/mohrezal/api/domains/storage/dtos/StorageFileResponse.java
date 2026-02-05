package com.github.mohrezal.api.domains.storage.dtos;

public record StorageFileResponse(byte[] data, String mimeType, String filename) {}
