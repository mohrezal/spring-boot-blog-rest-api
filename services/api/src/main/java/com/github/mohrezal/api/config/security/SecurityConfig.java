package com.github.mohrezal.api.config.security;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.config.ratelimit.RateLimitFilter;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_SWAGGER_PATHS = {
        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };

    private static final String[] PUBLIC_ACTUATOR_PATHS = {
        "/actuator/health", "/actuator/info", "/actuator/*"
    };

    private static final String[] PUBLIC_POST_ENDPOINTS = {
        Routes.build(Routes.Auth.BASE, Routes.Auth.REGISTER),
        Routes.build(Routes.Auth.BASE, Routes.Auth.LOGIN),
        Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH),
        Routes.build(Routes.Post.BASE, Routes.Post.VIEW),
    };

    private static final String[] PUBLIC_GET_ENDPOINTS = {
        Routes.build(Routes.Auth.BASE, Routes.Auth.CSRF),
        Routes.build(Routes.Category.BASE),
        Routes.build(Routes.Post.BASE),
        Routes.build(Routes.Post.BASE, Routes.Post.BY_SLUG),
        Routes.build(Routes.Post.BASE, Routes.Post.SLUG_AVAILABILITY),
        Routes.build(Routes.Storage.BASE, Routes.Storage.BY_FILENAME),
        Routes.build(Routes.Post.BASE, Routes.Post.SEARCH),
        Routes.build(Routes.Auth.BASE, Routes.Auth.VERIFY_EMAIL)
    };

    private final ApplicationProperties applicationProperties;
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.spa().csrfTokenRepository(csrfTokenRepository()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(
                                                (request, response, authException) ->
                                                        response.sendError(
                                                                HttpServletResponse
                                                                        .SC_UNAUTHORIZED))
                                        .accessDeniedHandler(
                                                (request, response, accessDeniedException) ->
                                                        response.sendError(
                                                                HttpServletResponse.SC_FORBIDDEN)))
                .authorizeHttpRequests(
                        auth -> {
                            if (applicationProperties.security().swagger().publicEnabled()) {
                                auth.requestMatchers(PUBLIC_SWAGGER_PATHS).permitAll();
                            } else {
                                auth.requestMatchers(PUBLIC_SWAGGER_PATHS).denyAll();
                            }

                            auth.requestMatchers(PUBLIC_ACTUATOR_PATHS)
                                    .permitAll()
                                    .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS)
                                    .permitAll()
                                    .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
                                    .permitAll()
                                    .requestMatchers(
                                            HttpMethod.POST,
                                            Routes.build(Routes.Auth.BASE, Routes.Auth.LOGOUT))
                                    .authenticated()
                                    .anyRequest()
                                    .authenticated();
                        })
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        var csrfCookie = applicationProperties.security().csrf().cookie();
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath(csrfCookie.path());
        repository.setCookieCustomizer(
                cookie -> {
                    cookie.path(csrfCookie.path())
                            .secure(csrfCookie.secure())
                            .sameSite(csrfCookie.sameSite());
                });
        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(applicationProperties.security().allowedOrigin());
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username ->
                userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}
