package com.github.mohrezal.api.domains.users.services.authentication;

import com.github.mohrezal.api.domains.users.dtos.LoginRequest;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.common.email.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public User authenticate(LoginRequest body) {
        var email = EmailNormalizer.normalize(body.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, body.password()));
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
}
