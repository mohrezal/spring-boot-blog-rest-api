package com.github.mohrezal.springbootblogrestapi.domains.users.services.registration;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;

public interface RegistrationService {
    User register(RegisterUserRequest body, UserRole role);
}
