package com.github.mohrezal.springbootblogrestapi.domains.users.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }
}
