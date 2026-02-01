package com.github.mohrezal.springbootblogrestapi.domains.notifications.queries;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.queries.params.GetUserUnreadNotificationCountQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserUnreadNotificationCountQuery
        implements Query<GetUserUnreadNotificationCountQueryParams, Integer> {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    @Override
    public Integer execute(GetUserUnreadNotificationCountQueryParams params) {
        User user = (User) params.getUserDetails();
        return this.notificationRepository.countByRecipientAndIsRead(user, false);
    }
}
