package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.dtos.NotificationSummary;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.mappers.NotificationMapper;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.GetNotificationsQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.shared.abstracts.AuthenticatedQuery;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetNotificationsQuery
        extends AuthenticatedQuery<GetNotificationsQueryParams, PageResponse<NotificationSummary>> {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<NotificationSummary> execute(GetNotificationsQueryParams params) {
        validate(params);
        Pageable pageable =
                PageRequest.of(
                        params.page(), params.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage =
                this.notificationRepository.findByRecipient(user, pageable);
        return PageResponse.from(notificationPage, notificationMapper::toNotificationSummary);
    }
}
