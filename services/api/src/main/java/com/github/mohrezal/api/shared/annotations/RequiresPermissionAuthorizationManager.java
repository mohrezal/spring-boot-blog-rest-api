package com.github.mohrezal.api.shared.annotations;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class RequiresPermissionAuthorizationManager
        implements AuthorizationManager<MethodInvocation> {

    @Override
    public AuthorizationResult authorize(
            Supplier<? extends Authentication> authentication, MethodInvocation invocation) {
        var annotation = findAnnotation(invocation);
        if (annotation == null || annotation.value().length == 0) {
            return new AuthorizationDecision(false);
        }

        var granted =
                authentication.get().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
        return new AuthorizationDecision(
                Arrays.stream(annotation.value()).anyMatch(granted::contains));
    }

    private static RequiresPermission findAnnotation(MethodInvocation invocation) {
        var methodAnnotation =
                AnnotationUtils.findAnnotation(invocation.getMethod(), RequiresPermission.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        var target = invocation.getThis();
        if (target == null) {
            return null;
        }
        return AnnotationUtils.findAnnotation(target.getClass(), RequiresPermission.class);
    }
}
