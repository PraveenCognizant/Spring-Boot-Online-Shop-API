package com.example.springcore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ============================================================
 * SECURITY CONCEPT 05: JWT — JSON Web Token
 * ============================================================
 *
 * WHAT IS A JWT?
 * A JWT is a compact, self-contained string the server gives to the
 * client after login. The client sends it back with every request
 * to prove who they are — without needing a session or database lookup.
 *
 * JWT looks like this (3 parts separated by dots):
 *   eyJhbGciOiJIUzI1NiJ9   ← Part 1: Header  (algorithm info)
 *   .eyJ1c2VybmFtZSI6...   ← Part 2: Payload (username, expiry, roles)
 *   .SflKxwRJSMeKKF2...    ← Part 3: Signature (proves nobody tampered it)
 *
 * Paste your token at https://jwt.io to decode and see the contents.
 *
 * HOW IT WORKS (step by step):
 *
 *   1. User logs in → server verifies username + password
 *   2. Server creates a JWT with username + expiry → signs it with secret key
 *   3. Server sends token to client
 *   4. Client stores token (in localStorage or memory)
 *   5. Client sends token in every request:
 *         Authorization: Bearer eyJhbGci...
 *   6. Server reads token → verifies signature → extracts username
 *   7. Server knows WHO is making the request — no DB lookup needed!
 *
 * WHY JWT OVER SESSIONS?
 *   Session: server stores login info → does NOT scale (multiple servers)
 *   JWT:     server is stateless → any server can verify the token → scales perfectly
 *
 * SECRET KEY: Used to SIGN tokens. If someone gets this key,
 *             they can create fake tokens → keep it secret and long!
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;   // in milliseconds (86400000 = 24 hours)

    // ---- Generate a JWT token for a logged-in user ----
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())     // WHO this token belongs to
                .issuedAt(new Date())                   // WHEN it was created
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // WHEN it expires
                .signWith(getSigningKey())              // SIGN with secret so nobody can fake it
                .compact();                             // build the final string
    }

    // ---- Extract the username stored inside a JWT ----
    public String extractUsername(String token) {
        return getClaims(token).getSubject();   // "subject" = username we stored above
    }

    // ---- Check if token is valid and not expired ----
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ---- Private helpers ----

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // Parse the token and extract all claims (payload data)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())     // verify the signature using our secret
                .build()
                .parseSignedClaims(token)        // throws exception if token is invalid/expired
                .getPayload();
    }

    // Build the signing key from our secret string
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);    // HS256 algorithm needs min 32 bytes
    }
}
