package com.github.mohrezal.api.domains.notifications.queries;

import com.github.mohrezal.api.domains.notifications.queries.params.SubscribeNotificationStreamQueryParams;
import com.github.mohrezal.api.domains.notifications.services.sse.NotificationSseService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SubscribeNotificationStreamQuery
        extends AuthenticatedQuery<SubscribeNotificationStreamQueryParams, SseEmitter> {

    private final NotificationSseService sseService;

    @Override
    public SseEmitter execute(SubscribeNotificationStreamQueryParams params) {
        validate(params);

        var emitter = sseService.subscribe(user.getId());
        log.info("Subscribe notification stream query successful.");
        return emitter;
    }
}
