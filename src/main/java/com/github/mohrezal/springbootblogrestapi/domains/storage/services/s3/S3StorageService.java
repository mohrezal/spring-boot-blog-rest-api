package com.github.mohrezal.springbootblogrestapi.domains.storage.services.s3;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface S3StorageService {

    String upload(MultipartFile file, String key, String contentType) throws IOException;

    byte[] download(String key);
}
