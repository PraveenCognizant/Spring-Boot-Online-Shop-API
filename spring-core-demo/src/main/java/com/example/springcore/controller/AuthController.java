package com.example.springcore.controller;

import com.example.springcore.dto.ApiResponse;
import com.example.springcore.dto.auth.AuthResponse;
import com.example.springcore.dto.auth.LoginRequest;
import com.example.springcore.dto.auth.RegisterRequest;
import com.example.springcore.exception.BadRequestException;
import com.example.springcore.model.Role;
import com.example.springcore.model.User;
import com.example.springcore.repository.UserRepository;
import com.example.springcore.security.JwtUtil;
import com.example.springcore.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================
 * SECURITY CONCEPT 08: AuthController — Login & Register
 * ============================================================
 *
 * Two public endpoints that don't need a token:
 *   POST /auth/register  → Create a new account, get a token
 *   POST /auth/login     → Log in with existing account, get a token
 *
 * ---- REGISTER FLOW ----
 *   1. Client sends: { "username": "praveen", "password": "secret123" }
 *   2. Check username is not already taken
 *   3. Hash the password with BCrypt (NEVER store raw passwords)
 *   4. Save new User to database
 *   5. Generate a JWT token for the new user
 *   6. Return the token to the client
 *
 * ---- LOGIN FLOW ----
 *   1. Client sends: { "username": "praveen", "password": "secret123" }
 *   2. AuthenticationManager verifies credentials:
 *        a. Loads user from DB (via UserDetailsServiceImpl)
 *        b. Compares BCrypt hash: matches("secret123", storedHash) → true/false
 *        c. Throws BadCredentialsException if wrong
 *   3. Generate a JWT token
 *   4. Return the token to the client
 *
 * ---- AFTER LOGIN ----
 *   Client stores the token and sends it in EVERY request:
 *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    // ---- POST /auth/register ----
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        // Step 1: Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken");
        }

        // Step 2: Determine role (default = USER, can request ADMIN for demo)
        Role role = "admin".equalsIgnoreCase(request.getRole()) ? Role.ROLE_ADMIN : Role.ROLE_USER;

        // Step 3: Hash the password — NEVER save raw passwords!
        // "secret123" → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Step 4: Create and save the User entity
        User user = new User(request.getUsername(), hashedPassword, role);
        userRepository.save(user);

        // Step 5: Generate JWT token for the new user
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        // Step 6: Return the token
        AuthResponse authResponse = new AuthResponse(token, user.getUsername(), role.name());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully! Save your token.", authResponse));
    }

    // ---- POST /auth/login ----
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            // Step 1: Verify username + password using AuthenticationManager
            // This internally:
            //   → calls UserDetailsServiceImpl.loadUserByUsername()
            //   → calls BCrypt.matches(rawPassword, storedHash)
            //   → throws BadCredentialsException if wrong
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Wrong username or password
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password"));
        }

        // Step 2: Credentials are correct — load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // Step 3: Generate JWT token
        String token = jwtUtil.generateToken(userDetails);

        // Step 4: Get role name to include in response
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        // Step 5: Return token
        AuthResponse authResponse = new AuthResponse(token, request.getUsername(), role);
        return ResponseEntity.ok(ApiResponse.success("Login successful! Use the token in Authorization header.", authResponse));
    }
}
