package com.github.mohrezal.common.constants;

public final class RabbitMQConstants {
    private RabbitMQConstants() {}

    public static final class Header {
        public static final String MESSAGE_ID = "x-message-id";
    }

    public static final class Notification {
        public static final String EXCHANGE = "notification.exchange";
        public static final int MAX_PRIORITY = 10;

        public static final class Queue {
            public static final String EMAIL = "notification.email.queue";
            public static final String VERIFICATION_REMINDER_DELAY =
                    "notification.verification.reminder.delay.queue";
        }

        public static final class RoutingKey {
            public static final String EMAIL = "notification.email";
            public static final String TRANSACTIONAL_EMAIL =
                    "notification.transactional.email.route";
            public static final String VERIFICATION_REMINDER_DELAY =
                    "notification.verification.reminder.delay.route";
            public static final String VERIFICATION_REMINDER_CONSUME =
                    "notification.verification.reminder.consume.route";
        }
    }

    public static final class DeadLetter {
        public static final String EXCHANGE = "dead.letter.exchange";

        public static final class Queue {
            public static final String EMAIL = "dead.letter.email.queue";
        }

        public static final class RoutingKey {
            public static final String EMAIL = "dead.letter.email.route";
        }
    }
}
