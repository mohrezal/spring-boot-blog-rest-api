package com.github.mohrezal.api.domains.notifications.utils;

import com.github.mohrezal.api.domains.notifications.models.NotificationPreference;

public final class NotificationUtils {

    public static NotificationPreference defaultPreferences() {
        return NotificationPreference.builder().inAppEnabled(true).emailEnabled(true).build();
    }
}
