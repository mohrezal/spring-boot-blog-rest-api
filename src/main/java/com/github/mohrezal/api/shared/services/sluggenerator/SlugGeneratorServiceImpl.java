package com.github.mohrezal.api.shared.services.sluggenerator;

import com.github.mohrezal.api.shared.exceptions.types.UnexpectedException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SlugGeneratorServiceImpl implements SlugGeneratorService {

    private static final Integer MAX_ATTEMPTS = 100;
    private static final int SUFFIX_LENGTH = 4;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String getSlug(String text, Function<String, Boolean> existsChecker) {
        return getSlug(text, 100, existsChecker);
    }

    @Override
    public String getSlug(String text, int maxLength, Function<String, Boolean> existsChecker) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or blank");
        }
        String baseSlug = getNormalizedText(text);

        if (baseSlug.isEmpty()) {
            baseSlug = getSuffix(20);
        }

        String slug = baseSlug;

        int attempts = 0;

        while (existsChecker.apply(slug)) {
            String suffix = getSuffix(SUFFIX_LENGTH);
            slug = baseSlug + "-" + suffix;
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                log.warn("Maximum attempts exceeds");
                throw new UnexpectedException();
            }
        }
        return slug;
    }

    @Override
    public String getRandomSlug(int length, Function<String, Boolean> existsChecker) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }
        if (existsChecker == null) {
            throw new IllegalArgumentException("Exists checker cannot be null");
        }

        String slug = getSuffix(length);
        int attempts = 0;

        while (existsChecker.apply(slug)) {
            slug = getSuffix(length);
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                log.warn("Maximum attempts exceeds");
                throw new UnexpectedException();
            }
        }

        return slug;
    }

    private String getSuffix(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            suffix.append(characters.charAt(index));
        }
        return suffix.toString();
    }

    private String getNormalizedText(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .toLowerCase()
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "-")
                .replaceAll("_", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-+|-+$", "");
    }
}
