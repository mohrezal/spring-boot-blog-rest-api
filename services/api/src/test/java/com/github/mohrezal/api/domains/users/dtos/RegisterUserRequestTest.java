package com.github.mohrezal.api.domains.users.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RegisterUserRequestTest {

    @Test
    void constructor_shouldNormalizeEmailAliases() {
        var request =
                new RegisterUserRequest(
                        "John", "Doe", "User+News@Gmail.com", "john_doe", "Password!123", "Hey.");

        assertEquals("user@gmail.com", request.email());
    }
}
