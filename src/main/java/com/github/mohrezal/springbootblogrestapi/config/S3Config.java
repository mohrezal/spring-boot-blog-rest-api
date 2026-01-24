package com.github.mohrezal.springbootblogrestapi.config;

import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(ApplicationProperties properties) {
        var storage = properties.storage();

        return S3Client.builder()
                .endpointOverride(URI.create(storage.endpoint()))
                .region(Region.of(storage.region()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        storage.accessKey(), storage.secretKey())))
                .serviceConfiguration(
                        S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
