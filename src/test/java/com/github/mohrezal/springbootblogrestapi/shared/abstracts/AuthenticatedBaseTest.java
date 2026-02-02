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
class AuthenticatedBaseTest {

    record TestParams(UserDetails userDetails) implements AuthenticatedParams {
        @Override
        public UserDetails getUserDetails() {
            return userDetails;
        }
    }

    static class TestBase extends AuthenticatedBase<TestParams> {
        User getUser() {
            return user;
        }
    }

    private TestBase base;

    @BeforeEach
    void setUp() {
        base = new TestBase();
    }

    @Test
    void validate_whenUserDetailsIsNull_shouldThrowAccessDenied() {
        var params = new TestParams(null);
        assertThrows(AccessDeniedException.class, () -> base.validate(params));
    }

    @Test
    void validate_whenUserDetailsIsNotUser_shouldThrowAccessDenied() {
        UserDetails notAUser = mock(UserDetails.class);
        var params = new TestParams(notAUser);
        assertThrows(AccessDeniedException.class, () -> base.validate(params));
    }

    @Test
    void validate_whenUserDetailsIsValidUser_shouldSetUserField() {
        User user = aUser().withId(UUID.randomUUID()).build();
        var params = new TestParams(user);
        base.validate(params);
        assertThat(base.getUser()).isEqualTo(user);
    }
}
