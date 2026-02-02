package com.github.mohrezal.springbootblogrestapi.shared.abstracts;

import static com.github.mohrezal.springbootblogrestapi.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class AuthenticatedCommandTest {
    record TestParams(UserDetails userDetails) implements AuthenticatedParams {
        @Override
        public UserDetails getUserDetails() {
            return (UserDetails) userDetails;
        }
    }

    static class TestCommand extends AuthenticatedCommand<TestParams, String> {

        @Override
        public String execute(TestParams params) {
            return "";
        }
    }

    private TestCommand command;

    @BeforeEach
    void setUp() {
        command = new TestCommand();
    }

    @Test
    void validate_whenUserDetailsIsNull_shouldThrowAccessDenied() {
        var params = new TestParams(null);
        assertThrows(AccessDeniedException.class, () -> command.validate(params));
    }

    @Test
    void validate_whenUserDetailsIsNotUser_shouldThrowAccessDenied() {
        UserDetails notAUser = mock(UserDetails.class);
        var params = new TestParams(notAUser);
        assertThrows(AccessDeniedException.class, () -> command.validate(params));
    }

    @Test
    void validate_whenUserDetailsIsValidUser_shouldSetUserField() {
        User user = aUser().withId(UUID.randomUUID()).build();
        var params = new TestParams(user);
        command.validate(params);
        assertThat(command.user).isEqualTo(user);
    }
}
