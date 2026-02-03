package com.github.mohrezal.api.shared.services.sluggenerator;

import java.util.function.Function;

public interface SlugGeneratorService {
    String getSlug(String text, Function<String, Boolean> existsChecker);

    String getSlug(String text, int maxLength, Function<String, Boolean> existsChecker);
}
