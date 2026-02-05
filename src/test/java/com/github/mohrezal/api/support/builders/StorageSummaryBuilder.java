package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.models.Storage;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class StorageSummaryBuilder {

    private UUID id;
    private String filename;
    private String originalFilename;
    private String mimeType;
    private Long size;
    private String title;
    private String alt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private StorageSummaryBuilder() {}

    public static StorageSummaryBuilder aStorageSummary(Storage storage) {
        StorageSummaryBuilder builder = new StorageSummaryBuilder();

        builder.id = storage.getId();
        builder.filename = storage.getFilename();
        builder.originalFilename = storage.getOriginalFilename();
        builder.mimeType = storage.getMimeType();
        builder.size = storage.getSize();
        builder.title = storage.getTitle();
        builder.alt = storage.getAlt();
        builder.createdAt = storage.getCreatedAt();
        builder.updatedAt = storage.getUpdatedAt();

        return builder;
    }

    public static StorageSummaryBuilder aStorageSummary() {
        StorageSummaryBuilder builder = new StorageSummaryBuilder();

        builder.id = UUID.randomUUID();
        builder.filename = "test-file.jpg";
        builder.originalFilename = "original.jpg";
        builder.mimeType = "image/jpeg";
        builder.size = 1024L;
        builder.title = "Test Image";
        builder.alt = "Test Alt";
        builder.createdAt = OffsetDateTime.now();
        builder.updatedAt = OffsetDateTime.now();

        return builder;
    }

    public StorageSummaryBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public StorageSummaryBuilder withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public StorageSummaryBuilder withOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
        return this;
    }

    public StorageSummaryBuilder withMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public StorageSummaryBuilder withSize(Long size) {
        this.size = size;
        return this;
    }

    public StorageSummaryBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public StorageSummaryBuilder withAlt(String alt) {
        this.alt = alt;
        return this;
    }

    public StorageSummaryBuilder withCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public StorageSummaryBuilder withUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public StorageSummary build() {
        return new StorageSummary(
                id, filename, originalFilename, mimeType, size, title, alt, createdAt, updatedAt);
    }
}
