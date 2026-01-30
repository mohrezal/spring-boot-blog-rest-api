package com.github.mohrezal.springbootblogrestapi.domains.notifications.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.services.sse.NotificationSseService;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.annotations.IsAdminOrUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(Routes.Notification.BASE)
@RequiredArgsConstructor
@Tag(name = "Notification")
public class NotificationController {

    private final NotificationSseService sseService;

    @IsAdminOrUser
    @GetMapping(value = Routes.Notification.STREAM, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal User user) {
        return sseService.subscribe(user.getId());
    }
}
