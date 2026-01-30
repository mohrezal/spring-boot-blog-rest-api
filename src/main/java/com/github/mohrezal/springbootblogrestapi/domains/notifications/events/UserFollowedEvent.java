package com.github.mohrezal.springbootblogrestapi.domains.notifications.events;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.util.UUID;

public record UserFollowedEvent(
        UUID actorId, String actorName, String actorHandle, User recipient) {}
