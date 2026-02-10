package com.github.mohrezal.api.domains.users.services.authentication;

import com.github.mohrezal.api.domains.users.dtos.LoginRequest;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public User authenticate(LoginRequest body) {
        try {
            var authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(body.email(), body.password()));
            log.info("The user with email={} successfully authenticated.", body.email());

            return userRepository.findByEmail(body.email()).orElseThrow(UserNotFoundException::new);
        } catch (UserNotFoundException ex) {
            log.warn(
                    "Authentication failed - the provided email is not found. - message={}",
                    ex.getMessage());
            throw ex;
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed - invalid credentials. - message={}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(
                    "Unexpected exception in AuthenticationService - message={}", ex.getMessage());
            throw ex;
        }
    }
}
