package com.github.mohrezal.api.domains.users.services.registration;

import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.models.User;

public interface RegistrationService {
    User register(RegisterUserRequest body, UserRole role);
}
