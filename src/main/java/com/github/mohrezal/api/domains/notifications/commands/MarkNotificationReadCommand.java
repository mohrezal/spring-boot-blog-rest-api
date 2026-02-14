package com.github.mohrezal.api.domains.notifications.commands;

import com.github.mohrezal.api.domains.notifications.commands.params.MarkNotificationReadCommandParams;
import com.github.mohrezal.api.domains.notifications.exceptions.context.NotificationMarkReadExceptionContext;
import com.github.mohrezal.api.domains.notifications.exceptions.types.NotificationNotFoundException;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.time.OffsetDateTime;
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
public class MarkNotificationReadCommand
        extends AuthenticatedCommand<MarkNotificationReadCommandParams, Void> {

    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public Void execute(MarkNotificationReadCommandParams params) {
        validate(params);

        var context =
                new NotificationMarkReadExceptionContext(
                        getUserId(), params.notificationId().toString());

        var notification =
                notificationRepository
                        .findById(params.notificationId())
                        .orElseThrow(() -> new NotificationNotFoundException(context));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new AccessDeniedException(context);
        }

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(OffsetDateTime.now());
            notificationRepository.save(notification);
        }

        log.info("Mark notification read command successful.");
        return null;
    }
}
