package com.github.mohrezal.springbootblogrestapi.domains.storage.services.s3;

import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService {

    private final S3Client s3Client;
    private final ApplicationProperties properties;

    @Override
    public String upload(MultipartFile file, String key, String contentType) throws IOException {
        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(properties.storage().bucket())
                        .key(key)
                        .contentType(contentType)
                        .build();

        s3Client.putObject(
                request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return key;
    }

    @Override
    public byte[] download(String key) {
        GetObjectRequest request =
                GetObjectRequest.builder().bucket(properties.storage().bucket()).key(key).build();

        return s3Client.getObjectAsBytes(request).asByteArray();
    }
}
