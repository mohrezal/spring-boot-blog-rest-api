package com.github.mohrezal.common.worker.messaging;

import java.util.Map;

public record VerificationReminderMessage(String to, String token, Map<String, Object> variables) {}
