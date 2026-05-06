package com.github.mohrezal.common.worker.events;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID id, String firstName, String lastName, String email, String verificationUrl) {}
