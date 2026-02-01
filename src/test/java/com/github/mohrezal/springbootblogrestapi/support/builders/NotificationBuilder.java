package com.github.mohrezal.springbootblogrestapi.support.builders;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.data.FollowNotificationData;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.data.NotificationData;
import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.Notification;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.time.OffsetDateTime;
import java.util.UUID;

public class NotificationBuilder {

    private UUID id;
    private User recipient;
    private User actor;
    private NotificationData data;
    private Boolean isRead = false;
    private OffsetDateTime readAt;

    public static NotificationBuilder aNotification() {
        return new NotificationBuilder();
    }

    public NotificationBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public NotificationBuilder withRecipient(User recipient) {
        this.recipient = recipient;
        return this;
    }

    public NotificationBuilder withActor(User actor) {
        this.actor = actor;
        return this;
    }

    public NotificationBuilder withData(NotificationData data) {
        this.data = data;
        return this;
    }

    public NotificationBuilder withFollowData(UUID actorId) {
        this.data = new FollowNotificationData(actorId);
        return this;
    }

    public NotificationBuilder withIsRead(Boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public NotificationBuilder withReadAt(OffsetDateTime readAt) {
        this.readAt = readAt;
        return this;
    }

    public NotificationBuilder asRead() {
        this.isRead = true;
        this.readAt = OffsetDateTime.now();
        return this;
    }

    public Notification build() {
        Notification notification =
                Notification.builder()
                        .recipient(recipient)
                        .actor(actor)
                        .data(data)
                        .isRead(isRead)
                        .readAt(readAt)
                        .build();
        if (id != null) {
            notification.setId(id);
        }
        return notification;
    }
}
