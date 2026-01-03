package com.github.mohrezal.springbootblogrestapi.domains.users.services.authentication;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.LoginRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;

public interface AuthenticationService {
    User authenticate(LoginRequest body);
}
