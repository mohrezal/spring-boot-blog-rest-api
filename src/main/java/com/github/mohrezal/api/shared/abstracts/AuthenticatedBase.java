package com.github.mohrezal.api.shared.abstracts;

import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import java.util.UUID;

public abstract class AuthenticatedBase<P extends AuthenticatedParams> {

    protected User user;

    public void validate(P params) {
        Object userDetails = params.getUserDetails();

        if (userDetails == null) {
            throw new AccessDeniedException();
        }

        if (!(userDetails instanceof User)) {
            throw new AccessDeniedException();
        }

        user = (User) userDetails;
    }

    public UUID getUserId() {
        return user.getId() != null ? user.getId() : null;
    }
}
