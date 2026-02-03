package com.github.mohrezal.api.config.security;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.config.ratelimit.RateLimitFilter;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_SWAGGER_PATHS = {
        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };

    private static final String[] PUBLIC_ACTUATOR_PATHS = {"/actuator/health", "/actuator/info"};

    private static final String[] PUBLIC_POST_ENDPOINTS = {
        Routes.build(Routes.Auth.BASE, Routes.Auth.REGISTER),
        Routes.build(Routes.Auth.BASE, Routes.Auth.LOGIN),
        Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH),
    };

    private static final String[] PUBLIC_GET_ENDPOINTS = {
        Routes.build(Routes.Category.BASE),
        Routes.build(Routes.Post.BASE),
        Routes.build(Routes.Post.BASE, Routes.Post.SLUG_AVAILABILITY),
        Routes.build(Routes.Storage.BASE, Routes.Storage.BY_FILENAME),
        Routes.build(Routes.Post.BASE, Routes.Post.SEARCH)
    };

    private final ApplicationProperties applicationProperties;
    private final CookieBearerTokenResolver cookieBearerTokenResolver;
    private final UserRepository userRepository;
    private final UserJwtAuthenticationConverter userJwtAuthenticationConverter;
    private final SkipJwtValidationFilter skipJwtValidationFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(PUBLIC_SWAGGER_PATHS)
                                        .permitAll()
                                        .requestMatchers(PUBLIC_ACTUATOR_PATHS)
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
                                        .authenticated())
                .addFilterBefore(skipJwtValidationFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.bearerTokenResolver(cookieBearerTokenResolver)
                                        .jwt(
                                                jwt ->
                                                        jwt.jwtAuthenticationConverter(
                                                                userJwtAuthenticationConverter)))
                .addFilterAfter(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
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
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes =
                java.util.Base64.getDecoder().decode(applicationProperties.security().secret());
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] keyBytes =
                java.util.Base64.getDecoder().decode(applicationProperties.security().secret());
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");
        return NimbusJwtEncoder.withSecretKey(secretKey).build();
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
