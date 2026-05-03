package com.github.mohrezal.worker.consumers;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import com.github.mohrezal.common.worker.messaging.TransactionalEmailMessage;
import com.github.mohrezal.worker.services.email.EmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalEmailConsumer {

    private final EmailProvider emailProvider;

    @RabbitListener(queues = RabbitMQConstants.TRANSACTIONAL_EMAIL_QUEUE)
    public void consume(TransactionalEmailMessage message) {
        log.debug("Received transactional email for: {}", message.to());

        emailProvider.send(
                message.to(), message.subject(), message.template(), message.variables());
        log.debug("Transactional email sent to {}", message.to());
    }
}
