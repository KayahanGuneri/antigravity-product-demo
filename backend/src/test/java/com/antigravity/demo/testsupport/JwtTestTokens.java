package com.antigravity.demo.testsupport;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to generate JWT tokens for integration tests.
 * Mirrors the logic in JwtService but exposed for test usage.
 */
public class JwtTestTokens {

    // Default secret from application.yml
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MINUTES = 60;

    public static String createAdminToken() {
        return createToken("admin@test.com", "ADMIN");
    }

    public static String createUserToken() {
        return createToken("user@test.com", "USER");
    }

    public static String createToken(String username, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MINUTES * 60 * 1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createExpiredToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject("user@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createTamperedToken() {
        String validToken = createUserToken();
        // Tamper with the payload (middle part) by changing a character
        String[] parts = validToken.split("\\.");
        String payload = parts[1];
        String tamperedPayload = payload.substring(0, payload.length() - 1) + (payload.endsWith("a") ? "b" : "a");
        return parts[0] + "." + tamperedPayload + "." + parts[2];
    }

    public static String createTokenWithoutRole() {
        return Jwts.builder()
                .setSubject("user@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MINUTES * 60 * 1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createTokenWithWrongSecret() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");

        // Use a different secret key
        String wrongSecret = "999E635266556A586E3272357538782F413F4428472B4B6250645367566B5999";
        byte[] keyBytes = Decoders.BASE64.decode(wrongSecret);
        Key wrongKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject("user@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MINUTES * 60 * 1000))
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
