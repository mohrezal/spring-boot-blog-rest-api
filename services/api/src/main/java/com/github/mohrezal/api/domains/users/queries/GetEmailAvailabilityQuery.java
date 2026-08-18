package com.github.mohrezal.api.domains.users.queries;

import com.github.mohrezal.api.domains.users.dtos.Availability;
import com.github.mohrezal.api.domains.users.dtos.EmailAvailabilityRequest;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailFormatException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.interfaces.Query;
import com.github.mohrezal.common.email.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetEmailAvailabilityQuery implements Query<EmailAvailabilityRequest, Availability> {

    private final UserRepository userRepository;

    @Override
    public void validate(EmailAvailabilityRequest request) {
        var email = request.email();
        if (!EmailNormalizer.isValid(email)
                || email.length() > EmailAvailabilityRequest.MAX_LENGTH) {
            throw new UserEmailFormatException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Availability execute(EmailAvailabilityRequest request) {
        validate(request);
        return new Availability(!userRepository.existsByEmail(request.email()));
    }
}
