package com.github.mohrezal.springbootblogrestapi.shared.abstracts;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;

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
}
