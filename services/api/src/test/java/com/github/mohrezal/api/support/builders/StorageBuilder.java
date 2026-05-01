package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.users.models.User;
import java.util.UUID;

public class StorageBuilder {

    private UUID id;
    private String filename = "test-file.jpg";
    private String originalFilename = "original.jpg";
    private String mimeType = "image/jpeg";
    private Long size = 1024L;
    private String title = "Test Image";
    private String alt = "Test Alt";
    private StorageType type = StorageType.MEDIA;
    private User user;

    public static StorageBuilder aStorage() {
        return new StorageBuilder();
    }

    public StorageBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public StorageBuilder withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public StorageBuilder withOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
        return this;
    }

    public StorageBuilder withMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public StorageBuilder withSize(Long size) {
        this.size = size;
        return this;
    }

    public StorageBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public StorageBuilder withAlt(String alt) {
        this.alt = alt;
        return this;
    }

    public StorageBuilder withType(StorageType type) {
        this.type = type;
        return this;
    }

    public StorageBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public Storage build() {
        Storage storage =
                Storage.builder()
                        .filename(filename)
                        .originalFilename(originalFilename)
                        .mimeType(mimeType)
                        .size(size)
                        .title(title)
                        .alt(alt)
                        .type(type)
                        .user(user)
                        .build();
        if (id != null) {
            storage.setId(id);
        }
        return storage;
    }
}
