package com.github.mohrezal.api.domains.notifications.services.sse;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mohrezal.api.domains.notifications.dtos.NotificationSummary;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class NotificationSseServiceImpl implements NotificationSseService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;
    private static final Duration USER_EMITTERS_TTL = Duration.ofMinutes(31);

    private final Cache<UUID, Set<SseEmitter>> userEmitters =
            Caffeine.newBuilder().expireAfterAccess(USER_EMITTERS_TTL).build();

    @Override
    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        userEmitters
                .asMap()
                .compute(
                        userId,
                        (id, emitters) -> {
                            Set<SseEmitter> updatedEmitters =
                                    emitters != null ? emitters : ConcurrentHashMap.newKeySet();
                            updatedEmitters.add(emitter);
                            return updatedEmitters;
                        });

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        log.debug("User {} subscribed to SSE notifications", userId);
        return emitter;
    }

    @Override
    public void push(UUID userId, NotificationSummary notification) {
        Set<SseEmitter> emitters = userEmitters.getIfPresent(userId);
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
            } catch (IOException | IllegalStateException e) {
                log.debug("Failed to send SSE to user {}: {}", userId, e.getMessage());
                removeEmitter(userId, emitter);
            }
        }
    }

    private void removeEmitter(UUID userId, SseEmitter emitter) {
        userEmitters
                .asMap()
                .computeIfPresent(
                        userId,
                        (id, emitters) -> {
                            emitters.remove(emitter);
                            return emitters.isEmpty() ? null : emitters;
                        });
        log.debug("Removed SSE emitter for user {}", userId);
    }
}
