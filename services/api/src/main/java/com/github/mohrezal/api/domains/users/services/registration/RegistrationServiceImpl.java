package com.github.mohrezal.api.domains.users.services.registration;

import com.github.mohrezal.api.domains.privilege.service.UserRoleAssignmentService;
import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailUnavailableException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleUnavailableException;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.models.UserCredentials;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRoleAssignmentService userRoleAssignmentService;

    @Override
    public User register(RegisterUserRequest registerUser) {
        if (userRepository.existsByHandle(registerUser.handle())) {
            throw new UserHandleUnavailableException();
        }
        if (userRepository.existsByEmail(registerUser.email())) {
            throw new UserEmailUnavailableException();
        }
        User newUser = this.userMapper.toUser(registerUser);
        String hashedPassword = passwordEncoder.encode(registerUser.password().trim());
        UserCredentials credentials =
                UserCredentials.builder().hashedPassword(hashedPassword).build();

        credentials.setUser(newUser);
        newUser.setCredentials(credentials);

        User savedUser = userRepository.save(newUser);
        userRoleAssignmentService.assignConfiguredUserRole(savedUser);
        return savedUser;
    }
}
