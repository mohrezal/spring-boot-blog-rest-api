package com.github.mohrezal.api.domains.notifications.messages;

import java.util.Map;

public record TransactionalEmailMessage(
        String to, String subject, String template, Map<String, Object> variables) {}
