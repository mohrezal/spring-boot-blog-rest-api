package com.github.mohrezal.worker.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record WorkerProperties(@Valid Mail mail, @Valid Reminder reminder) {

    @Validated
    public record Mail(@NotBlank String from) {}

    @Validated
    public record Reminder(@NotNull Duration ttl) {}
}
