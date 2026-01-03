package com.github.mohrezal.springbootblogrestapi.config;

import com.github.mohrezal.springbootblogrestapi.domains.users.repositories.UserRepository;
import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationProperties applicationProperties;
    private final CookieBearerTokenResolver cookieBearerTokenResolver;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                HttpMethod.POST,
                                                Routes.build(
                                                        Routes.Auth.BASE, Routes.Auth.REGISTER),
                                                Routes.build(Routes.Auth.BASE, Routes.Auth.LOGIN),
                                                Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH))
                                        .permitAll()
                                        .requestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.bearerTokenResolver(cookieBearerTokenResolver)
                                        .jwt(Customizer.withDefaults())
                                        .authenticationEntryPoint(
                                                (request, response, authException) -> {
                                                    response.sendError(
                                                            HttpServletResponse.SC_UNAUTHORIZED,
                                                            authException.getMessage());
                                                }))
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
                userRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
