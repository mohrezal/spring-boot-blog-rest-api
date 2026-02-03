package com.github.mohrezal.api.domains.users.services.userutils;

import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.models.User;

public interface UserUtilsService {

    boolean hasRole(User user, UserRole role);

    boolean isAdmin(User user);

    boolean isUser(User user);
}
