package com.github.mohrezal.api.domains.notifications.queries;

import com.github.mohrezal.api.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.api.domains.notifications.mappers.NotificationMapper;
import com.github.mohrezal.api.domains.notifications.queries.params.GetNotificationsQueryParams;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetNotificationsQuery
        extends AuthenticatedQuery<GetNotificationsQueryParams, PageResponse<NotificationSummary>> {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<NotificationSummary> execute(GetNotificationsQueryParams params) {
        var currentUser = getCurrentUser(params);

        var pageable =
                PageRequest.of(
                        params.page(), params.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        var notificationPage = this.notificationRepository.findByRecipient(currentUser, pageable);
        var response =
                PageResponse.from(notificationPage, notificationMapper::toNotificationSummary);
        log.info("Get notifications query successful.");
        return response;
    }
}
