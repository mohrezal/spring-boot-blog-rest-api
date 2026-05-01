package com.github.mohrezal.api.domains.notifications.services.sse;

import com.github.mohrezal.api.domains.notifications.dtos.NotificationSummary;
import java.util.UUID;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationSseService {
    SseEmitter subscribe(UUID userId);

    void push(UUID userId, NotificationSummary notification);
}
