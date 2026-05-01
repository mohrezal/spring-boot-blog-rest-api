package com.github.mohrezal.api.domains.storage.models;

import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "storage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Storage extends BaseModel {

    @Column(name = "filename", nullable = false, unique = true)
    private String filename;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "title")
    private String title;

    @Column(name = "alt")
    private String alt;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StorageType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
