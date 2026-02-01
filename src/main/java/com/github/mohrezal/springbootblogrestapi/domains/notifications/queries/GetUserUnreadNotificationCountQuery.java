package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.GetUserUnreadNotificationCountQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetUserUnreadNotificationCountQuery
        extends AuthenticatedQuery<GetUserUnreadNotificationCountQueryParams, Integer> {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    @Override
    public Integer execute(GetUserUnreadNotificationCountQueryParams params) {
        validate(params);
        return this.notificationRepository.countByRecipientAndIsRead(user, false);
    }
}
