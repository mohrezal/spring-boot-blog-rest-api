package com.github.mohrezal.api.domains.users.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.commands.params.VerifyEmailCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidVerificationTokenException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.utils.RedirectUrlUtils;
import com.github.mohrezal.common.redis.RedisCacheService;
import com.github.mohrezal.common.redis.constants.RedisKeyFactory;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifyEmailCommandTest {

    private static final String RAW_TOKEN = "raw-verification-token";

    @Mock private RedirectUrlUtils redirectUrlUtils;

    @Mock private RedisCacheService redisCacheService;

    @Mock private UserRepository userRepository;

    @InjectMocks private VerifyEmailCommand command;

    @Test
    void execute_whenTokenIsMissing_shouldNotExposeRawTokenInExceptionContext() {
        when(redirectUrlUtils.isValid(any())).thenReturn(true);
        when(redisCacheService.get(
                        eq(RedisKeyFactory.Verification.token(RAW_TOKEN)), eq(String.class)))
                .thenReturn(Optional.empty());

        var params = new VerifyEmailCommandParams(RAW_TOKEN, "http://localhost:3000");

        var ex =
                assertThrows(
                        UserInvalidVerificationTokenException.class, () -> command.execute(params));

        assertFalse(String.valueOf(ex.getContext()).contains(RAW_TOKEN));
        assertFalse(String.valueOf(ex.getMessage()).contains(RAW_TOKEN));
        verifyNoInteractions(userRepository);
    }
}
