package com.github.mohrezal.api.domains.notifications.queries;

import com.github.mohrezal.api.domains.notifications.queries.params.GetUserUnreadNotificationCountQueryParams;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class GetUserUnreadNotificationCountQuery
        extends AuthenticatedQuery<GetUserUnreadNotificationCountQueryParams, Integer> {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    @Override
    public Integer execute(GetUserUnreadNotificationCountQueryParams params) {
        validate(params);

        var unreadCount = this.notificationRepository.countByRecipientAndIsRead(user, false);
        log.info("Get user unread notification count query successful.");
        return unreadCount;
    }
}
