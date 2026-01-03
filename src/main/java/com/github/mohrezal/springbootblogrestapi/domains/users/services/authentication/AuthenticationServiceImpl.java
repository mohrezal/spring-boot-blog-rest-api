package com.github.mohrezal.springbootblogrestapi.domains.users.services.authentication;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.LoginRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public User authenticate(LoginRequest body) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                body.getEmail(), body.getPassword()));

        return userRepository.findByEmail(body.getEmail()).orElseThrow(UserNotFoundException::new);
    }
}
