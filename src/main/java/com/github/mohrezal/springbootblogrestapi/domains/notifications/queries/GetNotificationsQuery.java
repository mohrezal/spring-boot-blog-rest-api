package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers.NotificationMapper;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.GetNotificationsQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNotificationsQuery
        implements Query<GetNotificationsQueryParams, PageResponse<NotificationSummary>> {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<NotificationSummary> execute(GetNotificationsQueryParams params) {
        User user = (User) params.getUserDetails();
        Pageable pageable =
                PageRequest.of(
                        params.getPage(),
                        params.getSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage =
                this.notificationRepository.findByRecipient(user, pageable);
        return PageResponse.from(notificationPage, notificationMapper::toNotificationSummary);
    }
}
