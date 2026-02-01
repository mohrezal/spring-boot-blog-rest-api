package com.github.mohrezal.springbootblogrestapi.domains.notifications.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.GetNotificationsQuery;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.SubscribeNotificationStreamQuery;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.GetNotificationsQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.SubscribeNotificationStreamQueryParams;
import com.github.mohrezal.springbootblogrestapi.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(Routes.Notification.BASE)
@RequiredArgsConstructor
@Tag(name = "Notification")
public class NotificationController {

    private final SubscribeNotificationStreamQuery subscribeNotificationStreamQuery;
    private final GetNotificationsQuery getNotificationsQuery;

    @IsAdminOrUser
    @GetMapping(value = Routes.Notification.STREAM, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal UserDetails userDetails) {
        var params =
                SubscribeNotificationStreamQueryParams.builder().userDetails(userDetails).build();
        return subscribeNotificationStreamQuery.execute(params);
    }

    @IsAdminOrUser
    @GetMapping
    public PageResponse<NotificationSummary> getNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        var params =
                GetNotificationsQueryParams.builder()
                        .page(page)
                        .size(size)
                        .userDetails(userDetails)
                        .build();
        return getNotificationsQuery.execute(params);
    }
}
