package com.github.mohrezal.api.domains.users.queries;

import com.github.mohrezal.api.domains.users.dtos.Availability;
import com.github.mohrezal.api.domains.users.dtos.HandleAvailabilityRequest;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleFormatException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.interfaces.Query;
import com.github.mohrezal.common.constants.RegexUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetHandleAvailabilityQuery implements Query<HandleAvailabilityRequest, Availability> {

    private final UserRepository userRepository;
    private final ApplicationProperties applicationProperties;

    @Override
    public void validate(HandleAvailabilityRequest request) {
        var handle = request.handle();
        if (handle == null
                || handle.length() < HandleAvailabilityRequest.MIN_LENGTH
                || handle.length() > HandleAvailabilityRequest.MAX_LENGTH
                || !handle.matches(RegexUtils.HANDLE_PATTERN)) {
            throw new UserHandleFormatException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Availability execute(HandleAvailabilityRequest request) {
        validate(request);
        var handle = request.handle();
        if (applicationProperties.handle().reservedHandles().contains(handle)
                || userRepository.existsByHandle(handle)) {
            return new Availability(false);
        }
        return new Availability(true);
    }
}
