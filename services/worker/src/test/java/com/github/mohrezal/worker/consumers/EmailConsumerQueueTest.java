package com.github.mohrezal.worker.consumers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import com.github.mohrezal.common.worker.messaging.TransactionalEmailMessage;
import com.github.mohrezal.common.worker.messaging.VerificationReminderMessage;
import com.rabbitmq.client.Channel;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

class EmailConsumerQueueTest {

    @Test
    void consumers_shouldListenOnSeparateQueues() throws Exception {
        var transactionalQueue =
                listenerQueue(
                        TransactionalEmailConsumer.class.getDeclaredMethod(
                                "consume",
                                TransactionalEmailMessage.class,
                                Channel.class,
                                long.class,
                                String.class));
        var lowPriorityQueue =
                listenerQueue(
                        TransactionalEmailConsumer.class.getDeclaredMethod(
                                "consumeLowPriority",
                                TransactionalEmailMessage.class,
                                Channel.class,
                                long.class,
                                String.class));
        var reminderQueue =
                listenerQueue(
                        VerificationReminderConsumer.class.getDeclaredMethod(
                                "consume",
                                VerificationReminderMessage.class,
                                Channel.class,
                                long.class,
                                String.class));

        assertEquals(RabbitMQConstants.Notification.Queue.EMAIL, transactionalQueue);
        assertEquals(RabbitMQConstants.Notification.Queue.EMAIL_LOW, lowPriorityQueue);
        assertEquals(RabbitMQConstants.Notification.Queue.VERIFICATION_REMINDER, reminderQueue);
        assertNotEquals(transactionalQueue, lowPriorityQueue);
        assertNotEquals(transactionalQueue, reminderQueue);
    }

    private static String listenerQueue(Method consume) {
        return consume.getAnnotation(RabbitListener.class).queues()[0];
    }
}
