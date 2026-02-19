package com.github.mohrezal.api.shared.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        @Valid Security security, @Valid Storage storage, @Valid Handle handle, @Valid Mail mail) {

    @Validated
    public record Security(
            @NotBlank String secret,
            @NotNull @DurationMin(seconds = 60) Duration accessTokenLifeTime,
            @NotNull @DurationMin(seconds = 60) Duration refreshTokenLifeTime,
            @NotEmpty List<@NotBlank String> allowedOrigin) {}

    @Validated
    public record Storage(
            @NotEmpty List<@NotBlank String> allowedMimeTypes,
            @NotNull DataSize maxFileSize,
            @NotBlank String endpoint,
            @NotBlank String accessKey,
            @NotBlank String secretKey,
            @NotBlank String bucket,
            @NotBlank String region) {}

    @Validated
    public record Handle(@NotEmpty List<@NotBlank String> reservedHandles) {}

    @Validated
    public record Mail(@NotBlank String from) {}
}
