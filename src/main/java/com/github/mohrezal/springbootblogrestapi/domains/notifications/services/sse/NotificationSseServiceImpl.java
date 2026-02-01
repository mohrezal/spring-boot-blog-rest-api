package com.github.mohrezal.springbootblogrestapi.domains.notifications.services.sse;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationSummary;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class NotificationSseServiceImpl implements NotificationSseService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    private final Map<UUID, Set<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        Set<SseEmitter> emitters =
                userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>());
        emitters.add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        log.debug("User {} subscribed to SSE notifications", userId);
        return emitter;
    }

    @Override
    public void push(UUID userId, NotificationSummary notification) {
        Set<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            log.debug("No active SSE connections for user {}", userId);
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(notification.id().toString())
                                .name("notification")
                                .data(notification));
                log.debug("Pushed notification {} to user {}", notification.id(), userId);
            } catch (IOException e) {
                log.warn("Failed to send SSE to user {}: {}", userId, e.getMessage());
                removeEmitter(userId, emitter);
            }
        }
    }

    private void removeEmitter(UUID userId, SseEmitter emitter) {
        Set<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
        }
        log.debug("Removed SSE emitter for user {}", userId);
    }
}
