package com.github.mohrezal.api.domains.notifications.models;

import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "notification_preferences",
        indexes = {
            @Index(name = "idx_user_id", columnList = "user_id"),
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationPreference extends BaseModel {
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "in_app_enabled")
    @Builder.Default
    private Boolean inAppEnabled = true;

    @Column(name = "email_enabled")
    @Builder.Default
    private Boolean emailEnabled = true;
}
