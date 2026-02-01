package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.SubscribeNotificationStreamQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.services.sse.NotificationSseService;
import com.github.mohrezal.springbootblogrestapi.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubscribeNotificationStreamQuery
        extends AuthenticatedQuery<SubscribeNotificationStreamQueryParams, SseEmitter> {

    private final NotificationSseService sseService;

    @Override
    public SseEmitter execute(SubscribeNotificationStreamQueryParams params) {
        validate(params);
        return sseService.subscribe(user.getId());
    }
}
