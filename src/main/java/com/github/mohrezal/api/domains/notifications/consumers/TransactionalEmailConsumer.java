package com.github.mohrezal.api.domains.notifications.consumers;

import com.github.mohrezal.api.domains.notifications.config.RabbitMQConfig;
import com.github.mohrezal.api.domains.notifications.messages.TransactionalEmailMessage;
import com.github.mohrezal.api.domains.notifications.services.email.EmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalEmailConsumer {

    private final EmailProvider emailProvider;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTIONAL_EMAIL_QUEUE)
    public void consume(TransactionalEmailMessage message) {
        log.debug("Received transactional email for: {}", message.to());

        try {
            emailProvider.send(
                    message.to(), message.subject(), message.template(), message.variables());
            log.debug("Transactional email sent to {}", message.to());
        } catch (Exception e) {
            log.error("Failed to send transactional email to {}: {}", message.to(), e.getMessage());
        }
    }
}
