package com.github.mohrezal.api.shared.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticatedParams {
    UserDetails getUserDetails();
}
