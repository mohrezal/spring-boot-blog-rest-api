package com.github.mohrezal.common.constants;

public final class RabbitMQConstants {
    private RabbitMQConstants() {}

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    public static final String EMAIL_QUEUE = "notification.email.queue";
    public static final String TRANSACTIONAL_EMAIL_QUEUE = "transactional.email.queue";

    public static final String EMAIL_ROUTING_KEY = "notification.email";
    public static final String TRANSACTIONAL_EMAIL_ROUTING_KEY = "transactional.email";

    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";

    public static final String DEAD_EMAIL_ROUTING_KEY = "dead.notification.email";
    public static final String DEAD_TRANSACTIONAL_EMAIL_ROUTING_KEY = "dead.transactional.email";

    public static final String DEAD_EMAIL_QUEUE = "dead.notification.email.queue";
    public static final String DEAD_TRANSACTIONAL_EMAIL_QUEUE = "dead.transactional.email.queue";
}
