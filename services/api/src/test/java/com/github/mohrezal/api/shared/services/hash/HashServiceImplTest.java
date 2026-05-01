package com.github.mohrezal.api.shared.services.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HashServiceImplTest {

    private final HashService hashService = new HashServiceImpl();

    @Test
    void sha256_whenInputIsNull_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> hashService.sha256(null));
    }

    @Test
    void sha256_whenInputIsValid_shouldReturnExpectedHash() {
        String hash = hashService.sha256("hello");

        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", hash);
    }

    @Test
    void sha256_whenInputIsSame_shouldReturnSameHash() {
        String firstHash = hashService.sha256("sample-input");
        String secondHash = hashService.sha256("sample-input");

        assertEquals(firstHash, secondHash);
    }

    @Test
    void sha256_whenInputIsDifferent_shouldReturnDifferentHash() {
        String firstHash = hashService.sha256("sample-input-a");
        String secondHash = hashService.sha256("sample-input-b");

        assertNotEquals(firstHash, secondHash);
    }
}
