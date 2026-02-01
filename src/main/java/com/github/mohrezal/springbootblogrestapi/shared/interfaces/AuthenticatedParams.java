package com.github.mohrezal.springbootblogrestapi.shared.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticatedParams {
    UserDetails getUserDetails();
}
