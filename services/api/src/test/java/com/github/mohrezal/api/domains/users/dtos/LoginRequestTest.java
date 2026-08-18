package com.github.mohrezal.api.domains.users.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LoginRequestTest {

    @Test
    void constructor_shouldNormalizeEmailAliases() {
        var request = new LoginRequest("User+News@Gmail.com", "Password!123");

        assertEquals("user@gmail.com", request.email());
    }
}
