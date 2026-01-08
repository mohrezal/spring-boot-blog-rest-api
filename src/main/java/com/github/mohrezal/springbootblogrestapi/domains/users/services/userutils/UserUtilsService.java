package com.github.mohrezal.springbootblogrestapi.domains.users.services.userutils;

import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;

public interface UserUtilsService {

    boolean hasRole(User user, UserRole role);

    boolean isAdmin(User user);

    boolean isUser(User user);
}
