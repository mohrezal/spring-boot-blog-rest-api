package com.github.mohrezal.api.domains.users.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.dtos.EmailAvailabilityRequest;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailFormatException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.common.constants.MessageKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetEmailAvailabilityQueryTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private GetEmailAvailabilityQuery query;

    @Test
    void execute_whenEmailIsInvalid_shouldThrowFormatException() {
        var exception =
                assertThrows(
                        UserEmailFormatException.class,
                        () -> query.execute(new EmailAvailabilityRequest("not-an-email")));

        assertEquals(MessageKey.SHARED_VALIDATION_EMAIL, exception.getMessageKey());
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_whenEmailIsUnused_shouldReturnAvailable() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);

        var result = query.execute(new EmailAvailabilityRequest("user@example.com"));

        assertTrue(result.available());
    }

    @Test
    void execute_whenEmailAliasIsTaken_shouldReturnUnavailable() {
        when(userRepository.existsByEmail("user@gmail.com")).thenReturn(true);

        var result = query.execute(new EmailAvailabilityRequest("User+news@Gmail.com"));

        assertFalse(result.available());
        verify(userRepository).existsByEmail("user@gmail.com");
    }
}
