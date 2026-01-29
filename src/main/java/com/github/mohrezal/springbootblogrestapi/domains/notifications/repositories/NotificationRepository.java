package com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
        extends JpaRepository<@NonNull Notification, @NonNull UUID> {}
