package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.VerifyEmailCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.context.UserAlreadyVerifiedExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.context.UserNotFoundExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.types.UserAlreadyVerifiedException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidVerificationTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRedirectUrlException;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.utils.RedirectUrlUtils;
import com.github.mohrezal.common.redis.RedisCacheService;
import com.github.mohrezal.common.redis.constants.RedisKeyFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifyEmailCommand implements Command<VerifyEmailCommandParams, Void> {
    private final RedirectUrlUtils redirectUrlUtils;
    private final RedisCacheService redisCacheService;
    private final UserRepository userRepository;

    @Override
    public void validate(VerifyEmailCommandParams params) {
        if (!redirectUrlUtils.isValid(params.redirectUrl())) {
            throw new InvalidRedirectUrlException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(VerifyEmailCommandParams params) {
        validate(params);
        String key = RedisKeyFactory.Verification.token(params.token());
        var userId =
                redisCacheService
                        .get(key, String.class)
                        .orElseThrow(UserInvalidVerificationTokenException::new);
        var uuid = UUID.fromString(userId);
        var user =
                userRepository
                        .findById(uuid)
                        .orElseThrow(
                                () -> {
                                    var context = new UserNotFoundExceptionContext(uuid);
                                    return new UserNotFoundException(context);
                                });
        if (user.getIsVerified()) {
            var context = new UserAlreadyVerifiedExceptionContext(uuid);
            throw new UserAlreadyVerifiedException(context);
        }
        user.setIsVerified(true);
        userRepository.save(user);
        redisCacheService.delete(key);
        return null;
    }
}
