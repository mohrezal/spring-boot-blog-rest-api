package com.github.mohrezal.api.shared.abstracts;

import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;

public abstract class AuthenticatedBase<P extends AuthenticatedParams> {

    public User getCurrentUser(P params) {
        Object userDetails = params.getUserDetails();
        if (!(userDetails instanceof User user)) {
            throw new AccessDeniedException();
        }

        return user;
    }
}
