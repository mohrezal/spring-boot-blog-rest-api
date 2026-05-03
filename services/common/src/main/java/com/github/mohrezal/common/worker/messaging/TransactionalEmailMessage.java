package com.github.mohrezal.common.worker.messaging;

import java.util.Map;

public record TransactionalEmailMessage(
        String to, String subject, String template, Map<String, Object> variables) {}
