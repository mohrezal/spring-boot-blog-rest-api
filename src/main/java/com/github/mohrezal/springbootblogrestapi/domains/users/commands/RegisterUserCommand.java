package com.github.mohrezal.springbootblogrestapi.domains.users.commands;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterUser;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.services.registration.RegistrationService;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserCommand implements Command<RegisterUser, UserSummary> {

    private final RegistrationService registrationService;
    private final UserMapper userMapper;

    @Override
    public UserSummary execute(RegisterUser params) {
        User user = registrationService.register(params, UserRole.USER);
        return userMapper.toUserSummary(user);
    }
}
