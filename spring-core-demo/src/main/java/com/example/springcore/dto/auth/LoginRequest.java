package com.example.springcore.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * What the client sends when logging in.
 *
 * POST /auth/login
 * Body: { "username": "praveen", "password": "secret123" }
 */
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
