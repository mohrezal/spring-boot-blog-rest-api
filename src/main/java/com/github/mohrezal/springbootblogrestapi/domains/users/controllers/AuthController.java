package com.github.mohrezal.springbootblogrestapi.domains.users.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.RegisterUserCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterUser;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Auth.BASE)
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserCommand registerUserCommand;

    @PostMapping(Routes.Auth.REGISTER)
    public ResponseEntity<UserSummary> register(@Valid @RequestBody RegisterUser registerUser) {
        UserSummary response = registerUserCommand.execute(registerUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
