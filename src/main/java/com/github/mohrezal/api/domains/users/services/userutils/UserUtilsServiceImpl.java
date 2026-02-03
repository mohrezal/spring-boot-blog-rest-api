package com.github.mohrezal.api.domains.users.services.userutils;

import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserUtilsServiceImpl implements UserUtilsService {

    @Override
    public boolean hasRole(User user, UserRole role) {
        if (user == null || role == null) {
            return false;
        }
        return role.equals(user.getRole());
    }

    @Override
    public boolean isAdmin(User user) {
        return hasRole(user, UserRole.ADMIN);
    }

    @Override
    public boolean isUser(User user) {
        return hasRole(user, UserRole.USER);
    }
}
