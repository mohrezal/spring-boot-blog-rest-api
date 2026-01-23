package com.github.mohrezal.springbootblogrestapi.shared.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(@Valid Security security, @Valid Storage storage) {

    @Validated
    public record Security(
            @NotBlank String secret,
            @NotNull @Positive Long accessTokenLifeTime,
            @NotNull @Positive Long refreshTokenLifeTime,
            @NotEmpty List<@NotBlank String> allowedOrigin) {}

    @Validated
    public record Storage(
            @NotEmpty List<@NotBlank String> allowedExtensions, @NotNull DataSize maxFileSize) {}
}
