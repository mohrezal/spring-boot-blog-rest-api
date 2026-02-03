package com.github.mohrezal.api.domains.notifications.models;

import com.github.mohrezal.api.domains.notifications.data.NotificationData;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "notifications",
        indexes = {
            @Index(name = "idx_recipient_id", columnList = "recipient_id"),
            @Index(name = "idx_is_read", columnList = "is_read"),
            @Index(name = "idx_read_at", columnList = "read_at")
        })
@NamedEntityGraph(
        name = "Notification.withActor",
        attributeNodes = {@NamedAttributeNode(value = "actor", subgraph = "actor-avatar")},
        subgraphs = {
            @NamedSubgraph(name = "actor-avatar", attributeNodes = @NamedAttributeNode("avatar"))
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private NotificationData data;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private OffsetDateTime readAt;
}
