package com.github.mohrezal.api.domains.notifications.commands;

import com.github.mohrezal.api.domains.notifications.commands.params.MarkAllNotificationsReadCommandParams;
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
public class MarkAllNotificationsReadCommand
        extends AuthenticatedCommand<MarkAllNotificationsReadCommandParams, Void> {

    private final NotificationRepository notificationRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(MarkAllNotificationsReadCommandParams params) {
        validate(params);

        try {
            notificationRepository.markAllAsReadByRecipientId(user.getId(), OffsetDateTime.now());
            log.info("Mark all notifications read command successful.");
            return null;
        } catch (AccessDeniedException ex) {
            log.warn("Mark all notifications read command failed - message: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during mark all notifications read command operation", ex);
            throw ex;
        }
    }
}
