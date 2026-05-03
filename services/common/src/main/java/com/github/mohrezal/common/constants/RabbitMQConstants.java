package com.github.mohrezal.common.constants;

public final class RabbitMQConstants {
    private RabbitMQConstants() {}

    public static final String EXCHANGE = "notification.exchange";

    public static final String EMAIL_QUEUE = "notification.email.queue";
    public static final String TRANSACTIONAL_EMAIL_QUEUE = "transactional.email.queue";

    public static final String EMAIL_ROUTING_KEY = "notification.email";
    public static final String TRANSACTIONAL_EMAIL_ROUTING_KEY = "transactional.email";
}
