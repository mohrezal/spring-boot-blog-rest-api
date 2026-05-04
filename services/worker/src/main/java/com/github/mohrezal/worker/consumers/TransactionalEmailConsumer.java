package com.github.mohrezal.worker.consumers;

import com.github.mohrezal.common.constants.RabbitMQConstants;
import com.github.mohrezal.common.worker.messaging.TransactionalEmailMessage;
import com.github.mohrezal.worker.services.email.EmailProvider;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalEmailConsumer {

    private final EmailProvider emailProvider;

    @RabbitListener(
            queues = RabbitMQConstants.TRANSACTIONAL_EMAIL_QUEUE,
            containerFactory = "manualAckContainerFactory")
    public void consume(
            TransactionalEmailMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.debug("Received transactional email for: {}", message.to());

            emailProvider.send(
                    message.to(), message.subject(),
                    message.template(), message.variables());
            channel.basicAck(tag, false);
            log.info("Transactional email sent to {}", message.to());
        } catch (Exception e) {
            log.error("Failed to send email to {}", message.to(), e);

            throw new RuntimeException(e);
        }
    }
}
