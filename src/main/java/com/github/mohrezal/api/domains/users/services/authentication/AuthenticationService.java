package com.github.mohrezal.api.domains.users.services.authentication;

import com.github.mohrezal.api.domains.users.dtos.LoginRequest;
import com.github.mohrezal.api.domains.users.models.User;

public interface AuthenticationService {
    User authenticate(LoginRequest body);
}
