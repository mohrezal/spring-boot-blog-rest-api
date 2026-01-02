package com.github.mohrezal.springbootblogrestapi.domains.users.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Auth.BASE)
@RequiredArgsConstructor
public class AuthController {

    @PostMapping(Routes.Auth.REGISTER)
    public ResponseEntity<?> register() {

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
