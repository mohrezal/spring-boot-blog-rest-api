package com.github.mohrezal.worker.services.email;

import java.util.Map;

public interface EmailProvider {
    void send(String to, String subject, String templatePath, Map<String, Object> variables);
}
