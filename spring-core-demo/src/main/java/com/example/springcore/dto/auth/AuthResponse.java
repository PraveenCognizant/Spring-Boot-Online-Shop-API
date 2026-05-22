package com.example.springcore.dto.auth;

/**
 * What the server sends back after successful register or login.
 *
 * Response: {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "username": "praveen",
 *   "role": "ROLE_USER",
 *   "expiresIn": "24 hours"
 * }
 *
 * The client MUST save this token and send it in every future request:
 *   Header → Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 */
public class AuthResponse {

    private String token;
    private String username;
    private String role;
    private String expiresIn;

    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresIn = "24 hours";
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getExpiresIn() { return expiresIn; }
}
