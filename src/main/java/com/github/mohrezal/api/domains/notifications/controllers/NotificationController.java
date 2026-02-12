package com.github.mohrezal.api.domains.notifications.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.notifications.commands.MarkAllNotificationsReadCommand;
import com.github.mohrezal.api.domains.notifications.commands.MarkNotificationReadCommand;
import com.github.mohrezal.api.domains.notifications.commands.UpdateNotificationPreferencesCommand;
import com.github.mohrezal.api.domains.notifications.commands.params.MarkAllNotificationsReadCommandParams;
import com.github.mohrezal.api.domains.notifications.commands.params.MarkNotificationReadCommandParams;
import com.github.mohrezal.api.domains.notifications.commands.params.UpdateNotificationPreferencesCommandParams;
import com.github.mohrezal.api.domains.notifications.dtos.NotificationPreferenceSummary;
import com.github.mohrezal.api.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.api.domains.notifications.dtos.UpdateNotificationPreferenceRequest;
import com.github.mohrezal.api.domains.notifications.queries.GetNotificationPreferencesQuery;
import com.github.mohrezal.api.domains.notifications.queries.GetNotificationsQuery;
import com.github.mohrezal.api.domains.notifications.queries.GetUserUnreadNotificationCountQuery;
import com.github.mohrezal.api.domains.notifications.queries.SubscribeNotificationStreamQuery;
import com.github.mohrezal.api.domains.notifications.queries.params.GetNotificationPreferencesQueryParams;
import com.github.mohrezal.api.domains.notifications.queries.params.GetNotificationsQueryParams;
import com.github.mohrezal.api.domains.notifications.queries.params.GetUserUnreadNotificationCountQueryParams;
import com.github.mohrezal.api.domains.notifications.queries.params.SubscribeNotificationStreamQueryParams;
import com.github.mohrezal.api.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.api.shared.annotations.range.Range;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final GetUserUnreadNotificationCountQuery getUserUnreadNotificationCountQuery;
    private final GetNotificationPreferencesQuery getNotificationPreferencesQuery;
    private final MarkNotificationReadCommand markNotificationReadCommand;
    private final MarkAllNotificationsReadCommand markAllNotificationsReadCommand;
    private final UpdateNotificationPreferencesCommand updateNotificationPreferencesCommand;

    @IsAdminOrUser
    @GetMapping(value = Routes.Notification.STREAM, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal UserDetails userDetails) {
        var params =
                SubscribeNotificationStreamQueryParams.builder().userDetails(userDetails).build();
        return subscribeNotificationStreamQuery.execute(params);
    }

    @IsAdminOrUser
    @GetMapping
    public ResponseEntity<@NonNull PageResponse<NotificationSummary>> getNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @Range(max = 1000) @RequestParam(defaultValue = "0") int page,
            @Valid @Range(max = 20) @RequestParam(defaultValue = "20") int size) {
        var params =
                GetNotificationsQueryParams.builder()
                        .page(page)
                        .size(size)
                        .userDetails(userDetails)
                        .build();
        return ResponseEntity.ok().body(getNotificationsQuery.execute(params));
    }

    @IsAdminOrUser
    @GetMapping(Routes.Notification.UN_READ)
    public ResponseEntity<@NonNull Integer> getUnReadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        var params =
                GetUserUnreadNotificationCountQueryParams.builder()
                        .userDetails(userDetails)
                        .build();
        return ResponseEntity.ok().body(getUserUnreadNotificationCountQuery.execute(params));
    }

    @IsAdminOrUser
    @GetMapping(Routes.Notification.PREFERENCES)
    public ResponseEntity<NotificationPreferenceSummary> getNotificationPreference(
            @AuthenticationPrincipal UserDetails userDetails) {
        var params =
                GetNotificationPreferencesQueryParams.builder().userDetails(userDetails).build();
        return ResponseEntity.ok().body(getNotificationPreferencesQuery.execute(params));
    }

    @IsAdminOrUser
    @PatchMapping(Routes.Notification.MARK_READ)
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        var params = new MarkNotificationReadCommandParams(id, userDetails);
        markNotificationReadCommand.execute(params);
        return ResponseEntity.noContent().build();
    }

    @IsAdminOrUser
    @PatchMapping(Routes.Notification.MARK_ALL_READ)
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        var params = new MarkAllNotificationsReadCommandParams(userDetails);
        markAllNotificationsReadCommand.execute(params);
        return ResponseEntity.noContent().build();
    }

    @IsAdminOrUser
    @PutMapping(Routes.Notification.PREFERENCES)
    public ResponseEntity<NotificationPreferenceSummary> updateNotificationPreferences(
            @RequestBody UpdateNotificationPreferenceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var params = new UpdateNotificationPreferencesCommandParams(userDetails, request);
        return ResponseEntity.ok().body(updateNotificationPreferencesCommand.execute(params));
    }
}
