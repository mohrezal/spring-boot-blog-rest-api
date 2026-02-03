package com.github.mohrezal.api.domains.notifications.commands;

import com.github.mohrezal.api.domains.notifications.commands.params.MarkNotificationReadCommandParams;
import com.github.mohrezal.api.domains.notifications.exceptions.types.NotificationNotFoundException;
import com.github.mohrezal.api.domains.notifications.models.Notification;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarkNotificationReadCommand
        extends AuthenticatedCommand<MarkNotificationReadCommandParams, Void> {

    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public Void execute(MarkNotificationReadCommandParams params) {
        validate(params);
        Notification notification =
                notificationRepository
                        .findById(params.notificationId())
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
