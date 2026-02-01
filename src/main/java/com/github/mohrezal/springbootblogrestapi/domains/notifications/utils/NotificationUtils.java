package com.github.mohrezal.springbootblogrestapi.domains.notifications.utils;

import com.github.mohrezal.springbootblogrestapi.domains.notifications.models.NotificationPreference;

public final class NotificationUtils {

    public static NotificationPreference defaultPreferences() {
        return NotificationPreference.builder().inAppEnabled(true).emailEnabled(true).build();
    }
}
