package com.github.mohrezal.api.domains.users.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.dtos.HandleAvailabilityRequest;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleFormatException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.common.constants.MessageKey;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetHandleAvailabilityQueryTest {

    @Mock private UserRepository userRepository;

    private GetHandleAvailabilityQuery query;

    @BeforeEach
    void setUp() {
        var applicationProperties =
                new ApplicationProperties(
                        null,
                        null,
                        null,
                        new ApplicationProperties.Handle(List.of("admin", "owner")),
                        null,
                        null,
                        null);
        query = new GetHandleAvailabilityQuery(userRepository, applicationProperties);
    }

    @Test
    void execute_whenHandleIsInvalid_shouldThrowFormatException() {
        var exception =
                assertThrows(
                        UserHandleFormatException.class,
                        () -> query.execute(new HandleAvailabilityRequest("ab")));

        assertEquals(MessageKey.USER_VALIDATION_HANDLE_PATTERN, exception.getMessageKey());
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_whenHandleIsReserved_shouldReturnUnavailable() {
        var result = query.execute(new HandleAvailabilityRequest("Admin"));

        assertFalse(result.available());
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_whenHandleIsTaken_shouldReturnUnavailable() {
        when(userRepository.existsByHandle("john_doe")).thenReturn(true);

        var result = query.execute(new HandleAvailabilityRequest("John_Doe"));

        assertFalse(result.available());
        verify(userRepository).existsByHandle("john_doe");
    }

    @Test
    void execute_whenHandleIsUnused_shouldReturnAvailable() {
        when(userRepository.existsByHandle("john_doe")).thenReturn(false);

        var result = query.execute(new HandleAvailabilityRequest("john_doe"));

        assertTrue(result.available());
    }
}
