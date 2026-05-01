package com.github.mohrezal.api.shared.services.hash;

import com.github.mohrezal.api.shared.exceptions.types.UnexpectedException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HashServiceImpl implements HashService {

    @Override
    public String sha256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            log.error("Unexpected error happened on sha256: ", ex);
            throw new UnexpectedException();
        }
    }
}
