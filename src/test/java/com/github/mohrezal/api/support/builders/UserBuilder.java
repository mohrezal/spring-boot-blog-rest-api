package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.models.User;
import java.util.UUID;

public class UserBuilder {

    private UUID id;
    private String email = "test@example.com";
    private String handle = "testuser";
    private String firstName = "Test";
    private String lastName = "User";
    private String bio;
    private UserRole role = UserRole.USER;
    private Boolean isVerified = true;

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withHandle(String handle) {
        this.handle = handle;
        return this;
    }

    public UserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withBio(String bio) {
        this.bio = bio;
        return this;
    }

    public UserBuilder withRole(UserRole role) {
        this.role = role;
        return this;
    }

    public UserBuilder withIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
        return this;
    }

    public User build() {
        User user =
                User.builder()
                        .email(email)
                        .handle(handle)
                        .firstName(firstName)
                        .lastName(lastName)
                        .bio(bio)
                        .role(role)
                        .isVerified(isVerified)
                        .build();
        if (id != null) {
            user.setId(id);
        }
        return user;
    }
}
