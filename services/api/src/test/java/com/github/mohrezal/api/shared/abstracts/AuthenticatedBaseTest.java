package com.github.mohrezal.api.shared.abstracts;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
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

    static class TestBase extends AuthenticatedBase<TestParams> {}

    private TestBase base;

    @BeforeEach
    void setUp() {
        base = new TestBase();
    }

    @Test
    void getCurrentUser_whenUserDetailsIsNull_shouldThrowAccessDenied() {
        var params = new TestParams(null);
        assertThrows(AccessDeniedException.class, () -> base.getCurrentUser(params));
    }

    @Test
    void getCurrentUser_whenUserDetailsIsNotUser_shouldThrowAccessDenied() {
        UserDetails notAUser = mock(UserDetails.class);
        var params = new TestParams(notAUser);
        assertThrows(AccessDeniedException.class, () -> base.getCurrentUser(params));
    }

    @Test
    void getCurrentUser_whenUserDetailsIsValidUser_shouldReturnUser() {
        User user = aUser().withId(UUID.randomUUID()).build();
        var params = new TestParams(user);
        assertThat(base.getCurrentUser(params)).isEqualTo(user);
    }
}
