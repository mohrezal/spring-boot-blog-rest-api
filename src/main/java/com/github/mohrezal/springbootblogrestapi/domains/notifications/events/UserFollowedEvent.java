package com.github.mohrezal.springbootblogrestapi.domains.notifications.events;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;

public record UserFollowedEvent(User actor, User recipient) {}
