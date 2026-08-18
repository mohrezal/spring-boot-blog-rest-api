package com.github.mohrezal.api.config.security;

import com.github.mohrezal.api.shared.annotations.RequiresPermission;
import com.github.mohrezal.api.shared.annotations.RequiresPermissionAuthorizationManager;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;

@Configuration(proxyBeanMethods = false)
public class MethodSecurityConfig {

    @Bean
    static AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
        return new AnnotationTemplateExpressionDefaults();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static Advisor requiresPermissionAdvisor() {
        var pointcut = new AnnotationMatchingPointcut(null, RequiresPermission.class, true);
        var interceptor =
                new AuthorizationManagerBeforeMethodInterceptor(
                        pointcut, new RequiresPermissionAuthorizationManager());
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder());
        return interceptor;
    }
}
