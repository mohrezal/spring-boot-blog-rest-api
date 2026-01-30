package com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationPreferenceRepository
        extends JpaRepository<@NonNull NotificationPreference, @NonNull UUID> {

    Optional<NotificationPreference> findByUserId(UUID userId);
}
