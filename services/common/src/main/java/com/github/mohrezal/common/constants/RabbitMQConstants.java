package com.github.mohrezal.common.constants;

public final class RabbitMQConstants {
    private RabbitMQConstants() {}

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";

    public static final String NOTIFICATION_EMAIL_QUEUE = "notification.email.queue";
    public static final String NOTIFICATION_EMAIL_ROUTING_KEY = "notification.email";

    public static final String NOTIFICATION_TRANSACTIONAL_EMAIL_QUEUE =
            "notification.transactional.email.queue";
    public static final String NOTIFICATION_TRANSACTIONAL_EMAIL_ROUTING_KEY =
            "notification.transactional.email.route";

    public static final String NOTIFICATION_VERIFICATION_REMINDER_DELAY_QUEUE =
            "notification.verification.reminder.delay.queue";
    public static final String NOTIFICATION_VERIFICATION_REMINDER_DELAY_ROUTING_KEY =
            "notification.verification.reminder.delay.route";

    public static final String NOTIFICATION_VERIFICATION_REMINDER_QUEUE =
            "notification.verification.reminder.queue";
    public static final String NOTIFICATION_VERIFICATION_REMINDER_CONSUME_ROUTING_KEY =
            "notification.verification.reminder.consume.route";

    public static final String DEAD_LETTER_EMAIL_QUEUE = "dead.letter.email.queue";
    public static final String DEAD_LETTER_EMAIL_ROUTING_KEY = "dead.letter.email.route";

    public static final String DEAD_LETTER_TRANSACTIONAL_EMAIL_QUEUE =
            "dead.letter.transactional.email.queue";
    public static final String DEAD_LETTER_TRANSACTIONAL_EMAIL_ROUTING_KEY =
            "dead.letter.transactional.email.route";
}
