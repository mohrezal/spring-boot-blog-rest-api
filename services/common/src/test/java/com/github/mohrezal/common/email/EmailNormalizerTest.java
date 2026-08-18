package com.github.mohrezal.common.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EmailNormalizerTest {

    @Test
    void normalize_whenEmailIsNull_shouldReturnNull() {
        assertNull(EmailNormalizer.normalize(null));
    }

    @ParameterizedTest
    @CsvSource({
        "user@x.com, user@x.com",
        "User@X.com, user@x.com",
        "'  User@X.com  ', user@x.com",
        "user+tag@x.com, user@x.com",
        "User+News@Example.COM, user@example.com",
        "first.last+promo@company.com, first.last@company.com",
        "first.last@company.com, first.last@company.com",
        "u.s.er+tag@gmail.com, user@gmail.com",
        "U.S.Er@Gmail.Com, user@gmail.com",
        "u.s.er@googlemail.com, user@gmail.com",
        "user+news@googlemail.com, user@gmail.com"
    })
    void normalize_shouldCanonicalizeAliases(String input, String expected) {
        assertEquals(expected, EmailNormalizer.normalize(input));
    }
}
