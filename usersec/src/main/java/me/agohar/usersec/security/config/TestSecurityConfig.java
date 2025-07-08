package me.agohar.usersec.security.config;

import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test Security Configuration - Currently disabled to avoid filter chain conflicts.
 * Enable this when running tests by uncommenting @Configuration annotations.
 */
// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity(prePostEnabled = true)
@Profile("test") // Only active when 'test' profile is used
public class TestSecurityConfig {

    /**
     * Test security filter chain - currently disabled
     */
    // @Bean
    // @Order(1) // Higher precedence than the main SecurityConfig
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/v1/test/**") // Only match test endpoints
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(mockJwtDecoder())
                    .jwtAuthenticationConverter(testJwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    // @Bean
    public JwtDecoder mockJwtDecoder() {
        return token -> {
            // Parse the token manually for testing
            Map<String, Object> headers = new HashMap<>();
            headers.put("alg", "HS256");
            headers.put("typ", "JWT");

            Map<String, Object> claims = new HashMap<>();
            
            // Extract email from token (basic parsing)
            if (token.contains("admin")) {
                claims.put("sub", "admin@test.com");
                claims.put("email", "admin@test.com");
                claims.put("groups", List.of("ROLE_ADMIN"));
            } else if (token.contains("user")) {
                claims.put("sub", "user@test.com");
                claims.put("email", "user@test.com");
                claims.put("groups", List.of("ROLE_USER"));
            } else {
                claims.put("sub", "unknown@test.com");
                claims.put("email", "unknown@test.com");
                claims.put("groups", List.of("ROLE_USER"));
            }
            
            claims.put("iss", "https://accounts.google.com");
            claims.put("aud", "test-audience");
            claims.put("iat", Instant.now().getEpochSecond());
            claims.put("exp", Instant.now().plusSeconds(3600).getEpochSecond());

            return new Jwt(token, Instant.now(), Instant.now().plusSeconds(3600), headers, claims);
        };
    }

    // @Bean
    public JwtAuthenticationConverter testJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> groups = jwt.getClaimAsStringList("groups");
            return groups.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(java.util.stream.Collectors.toList());
        });
        return converter;
    }
}