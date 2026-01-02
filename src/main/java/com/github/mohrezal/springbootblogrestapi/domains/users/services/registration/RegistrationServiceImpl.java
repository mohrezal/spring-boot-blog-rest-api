package com.github.mohrezal.springbootblogrestapi.domains.users.services.registration;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterUser;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.UserEmailConflictException;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.UserCredentials;
import com.github.mohrezal.springbootblogrestapi.domains.users.repositories.UserRepository;
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
    public User register(RegisterUser registerUser, UserRole role) {
        if (userRepository.existsByEmail(registerUser.getEmail())) {
            throw new UserEmailConflictException();
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
