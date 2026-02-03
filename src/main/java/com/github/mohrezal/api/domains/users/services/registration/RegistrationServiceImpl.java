package com.github.mohrezal.api.domains.users.services.registration;

import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailAlreadyExistsException;
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

    @Override
    public User register(RegisterUserRequest registerUser, UserRole role) {
        if (userRepository.existsByEmail(registerUser.getEmail())) {
            throw new UserEmailAlreadyExistsException();
        }
        User newUser = this.userMapper.toUser(registerUser, role);
        String hashedPassword = passwordEncoder.encode(registerUser.getPassword().trim());
        UserCredentials credentials =
                UserCredentials.builder().hashedPassword(hashedPassword).build();

        credentials.setUser(newUser);
        newUser.setCredentials(credentials);

        return userRepository.save(newUser);
    }
}
