package com.github.mohrezal.common.email;

import com.github.mohrezal.common.constants.RegexUtils;
import java.util.Locale;
import java.util.Set;

public final class EmailNormalizer {

    private static final Set<String> GMAIL_DOMAINS = Set.of("gmail.com", "googlemail.com");

    private EmailNormalizer() {}

    public static boolean isValid(String email) {
        var normalized = normalize(email);
        return normalized != null && normalized.matches(RegexUtils.EMAIL_PATTERN);
    }

    public static String normalize(String email) {
        if (email == null) {
            return null;
        }

        var trimmed = email.trim().toLowerCase(Locale.ROOT);
        var at = trimmed.lastIndexOf('@');
        if (at <= 0 || at == trimmed.length() - 1) {
            return trimmed;
        }

        var local = trimmed.substring(0, at);
        var domain = trimmed.substring(at + 1);

        var plus = local.indexOf('+');
        if (plus >= 0) {
            local = local.substring(0, plus);
        }

        if (GMAIL_DOMAINS.contains(domain)) {
            local = local.replace(".", "");
            domain = "gmail.com";
        }

        return local + "@" + domain;
    }
}
