package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.SubscribeNotificationStreamQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.services.sse.NotificationSseService;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SubscribeNotificationStreamQuery
        implements Query<SubscribeNotificationStreamQueryParams, SseEmitter> {

    private final NotificationSseService sseService;

    @Override
    public SseEmitter execute(SubscribeNotificationStreamQueryParams params) {
        User user = (User) params.getUserDetails();
        return sseService.subscribe(user.getId());
    }
}
