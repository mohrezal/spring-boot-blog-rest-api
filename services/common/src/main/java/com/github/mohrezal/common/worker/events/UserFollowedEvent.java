package com.github.mohrezal.common.worker.events;

import com.github.mohrezal.common.worker.contracts.NotificationPreference;
import java.util.UUID;

public record UserFollowedEvent(
        UUID actorId,
        String actorHandle,
        UUID recipientId,
        String recipientHandle,
        String recipientEmail,
        NotificationPreference preferences) {}
