package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params.MarkAllNotificationsReadCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkAllNotificationsReadCommand
        implements Command<MarkAllNotificationsReadCommandParams, Void> {

    private final NotificationRepository notificationRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(MarkAllNotificationsReadCommandParams params) {
        User user = (User) params.getUserDetails();
        notificationRepository.markAllAsReadByRecipientId(user.getId(), OffsetDateTime.now());
        return null;
    }
}
