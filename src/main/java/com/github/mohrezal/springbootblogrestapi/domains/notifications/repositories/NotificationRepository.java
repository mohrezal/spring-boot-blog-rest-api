package com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
        extends JpaRepository<@NonNull Notification, @NonNull UUID> {

    @EntityGraph(value = "Notification.withActor")
    Page<Notification> findByRecipient(User recipient, Pageable pageable);

    Integer countByRecipientAndIsRead(User recipient, Boolean isRead);

    @Modifying
    @Query(
            "UPDATE Notification n SET n.isRead = true, n.readAt = :readAt "
                    + "WHERE n.recipient.id = :recipientId AND n.isRead = false")
    int markAllAsReadByRecipientId(UUID recipientId, OffsetDateTime readAt);
}
