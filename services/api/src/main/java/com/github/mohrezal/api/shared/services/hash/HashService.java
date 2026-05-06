package com.github.mohrezal.api.shared.services.hash;

import java.util.function.Function;

public interface HashService {
    String sha256(String input);

    String sha256(String input, Function<String, Boolean> existsChecker);
}
