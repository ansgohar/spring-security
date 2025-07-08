package me.agohar.usersec.security.test.util;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for generating JWT tokens for testing purposes
 * This should only be used in test environments
 */
public class JwtTestTokenGenerator {
    
    // Fixed secret key for testing - DO NOT USE IN PRODUCTION
    private static final String TEST_SECRET = "MyTestSecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345";
    private static final SecretKey SECRET_KEY = new SecretKeySpec(
        TEST_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
    );
    private static final long EXPIRATION_TIME = 3600000; // 1 hour
    
    /**
     * Get the secret key used for signing tokens
     * This method can be used by other components that need to validate the tokens
     */
    public static SecretKey getSecretKey() {
        return SECRET_KEY;
    }
    
    public static void main(String[] args) {
        System.out.println("=== JWT Test Token Generator ===\n");
        
        // Generate Admin Token
        String adminToken = generateAdminToken();
        System.out.println("ADMIN TOKEN:");
        System.out.println(adminToken);
        System.out.println("\nExport command:");
        System.out.println("export TEST_ADMIN_TOKEN=\"" + adminToken + "\"");
        
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Generate User Token
        String userToken = generateUserToken();
        System.out.println("USER TOKEN:");
        System.out.println(userToken);
        System.out.println("\nExport command:");
        System.out.println("export TEST_USER_TOKEN=\"" + userToken + "\"");
        
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Generate Manager Token
        String managerToken = generateManagerToken();
        System.out.println("MANAGER TOKEN:");
        System.out.println(managerToken);
        System.out.println("\nExport command:");
        System.out.println("export TEST_MANAGER_TOKEN=\"" + managerToken + "\"");
        
        System.out.println("\n=== Copy and paste the export commands above ===");
    }
    
    public static String generateAdminToken() {
        return generateToken(
            "a.gohar@eunx.com",
            "Admin User",
            List.of("ADMIN"),  // Remove ROLE_ prefix, Spring Security will add it
            Map.of(
                "department", "IT",
                "permissions", List.of("CREATE_USER", "DELETE_USER", "MODIFY_USER", "CREATE_GROUP", "DELETE_GROUP", "MODIFY_GROUP")
            )
        );
    }
    
    public static String generateUserToken() {
        return generateToken(
            "user@test.com", 
            "Regular User",
            List.of("USER"),  // Remove ROLE_ prefix, Spring Security will add it
            Map.of(
                "department", "Engineering",
                "permissions", List.of("READ_PROFILE", "UPDATE_PROFILE")
            )
        );
    }
    
    public static String generateManagerToken() {
        return generateToken(
            "manager@test.com",
            "Team Manager", 
            List.of("MANAGER"),  // Remove ROLE_ prefix, Spring Security will add it
            Map.of(
                "department", "Engineering",
                "permissions", List.of("CREATE_USER", "MODIFY_USER", "READ_USERS", "CREATE_GROUP", "MODIFY_GROUP")
            )
        );
    }
    
    public static String generateToken(String email, String name, List<String> roles, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("name", name);
        claims.put("groups", roles);  // Spring Security expects 'groups' claim for roles
        claims.put("iss", "https://accounts.google.com");
        claims.put("aud", "test-audience");
        
        // Add any additional claims
        claims.putAll(additionalClaims);
        
        return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SECRET_KEY)
            .compact();
    }
    
    /**
     * Generate a token for a specific user with custom roles
     */
    public static String generateCustomToken(String email, String name, List<String> roles) {
        return generateToken(email, name, roles, Map.of());
    }
    
    /**
     * Generate an expired token for testing token validation
     */
    public static String generateExpiredToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("groups", List.of("USER"));  // Remove ROLE_ prefix
        claims.put("iss", "https://accounts.google.com");
        claims.put("aud", "test-audience");
        
        return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date(System.currentTimeMillis() - EXPIRATION_TIME * 2))
            .expiration(new Date(System.currentTimeMillis() - EXPIRATION_TIME)) // Already expired
            .signWith(SECRET_KEY)
            .compact();
    }
    
    /**
     * Generate a token with invalid signature for testing
     */
    public static String generateInvalidSignatureToken(String email) {
        SecretKey wrongKey = Jwts.SIG.HS256.key().build();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("groups", List.of("USER"));  // Remove ROLE_ prefix
        claims.put("iss", "https://accounts.google.com");
        claims.put("aud", "test-audience");
        
        return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(wrongKey) // Using wrong key to create invalid signature
            .compact();
    }
}
