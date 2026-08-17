package com.github.mohrezal.api.domains.privilege.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityPermissionChecker {

    public boolean hasPermission(String key) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> key.equals(authority.getAuthority()));
    }
}
