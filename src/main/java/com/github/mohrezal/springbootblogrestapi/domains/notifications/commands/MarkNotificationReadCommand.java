package com.github.mohrezal.springbootblogrestapi.domains.notifications.commands;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.commands.params.MarkNotificationReadCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.exceptions.types.NotificationNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkNotificationReadCommand
        implements Command<MarkNotificationReadCommandParams, Void> {

    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public Void execute(MarkNotificationReadCommandParams params) {
        User user = (User) params.getUserDetails();

        Notification notification =
                notificationRepository
                        .findById(params.getNotificationId())
                        .orElseThrow(NotificationNotFoundException::new);

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new AccessDeniedException();
        }

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(OffsetDateTime.now());
            notificationRepository.save(notification);
        }

        return null;
    }
}
