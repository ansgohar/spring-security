package me.agohar.usersec.security.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    private final GoogleCredentials googleCredentials;

    public AuthenticationService(GoogleCredentials googleCredentials) {
        this.googleCredentials = googleCredentials;
    }

    public Map<String, Object> validateToken(String token) {
        try {
            // In a real implementation, you would validate the JWT token here
            // For now, we'll simulate token validation
            
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("valid", true);
            tokenInfo.put("email", "user@example.com");
            tokenInfo.put("roles", Collections.singletonList("USER"));
            
            return tokenInfo;
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String generateServiceToken(String targetAudience) {
        try {
            if (googleCredentials instanceof IdTokenProvider) {
                IdTokenProvider idTokenProvider = (IdTokenProvider) googleCredentials;
                IdTokenCredentials idTokenCredentials = 
                    IdTokenCredentials.newBuilder()
                        .setIdTokenProvider(idTokenProvider)
                        .setTargetAudience(targetAudience)
                        .build();
                
                idTokenCredentials.refresh();
                return idTokenCredentials.getAccessToken().getTokenValue();
            }
            
            throw new IllegalStateException("Credentials do not support ID token generation");
        } catch (IOException e) {
            logger.error("Error generating service token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate service token", e);
        }
    }
}
