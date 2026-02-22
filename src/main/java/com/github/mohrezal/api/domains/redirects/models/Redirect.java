package com.github.mohrezal.api.domains.redirects.models;

import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.shared.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "redirects",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_redirects_code", columnNames = "code"),
            @UniqueConstraint(
                    name = "uq_redirects_target",
                    columnNames = {"target_type", "target_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Redirect extends BaseModel {

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "target_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RedirectTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;
}
