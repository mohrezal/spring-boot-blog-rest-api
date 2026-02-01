package com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
        extends JpaRepository<@NonNull Notification, @NonNull UUID> {
    Page<Notification> findByRecipient(User recipient, Pageable pageable);

    Integer countByRecipientAndIsRead(User recipient, Boolean isRead);
}
