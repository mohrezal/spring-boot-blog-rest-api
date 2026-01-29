package com.github.mohrezal.springbootblogrestapi.domains.notifications.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

public interface RabbitMQConfig {

    Queue inAppQueue();

    Queue emailQueue();

    DirectExchange exchange();

    Binding inAppBinding();

    Binding emailBinding();
}
