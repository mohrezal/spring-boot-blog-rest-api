package com.github.mohrezal.worker.consumers;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadLetterInspector {
    @RabbitListener(queues = RabbitMQConstants.DeadLetter.Queue.EMAIL)
    public void handleDeadTransactionalEmail(Message failedMessage) {
        String body = new String(failedMessage.getBody());
        log.error("Dead‑lettered transactional email: {}", body);
    }
}
