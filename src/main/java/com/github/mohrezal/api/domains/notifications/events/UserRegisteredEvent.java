package com.github.mohrezal.api.domains.notifications.events;

import com.github.mohrezal.api.domains.users.models.User;

public record UserRegisteredEvent(User user) {}
