package com.example.springcore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ============================================================
 * SECURITY CONCEPT 06: JwtFilter — The Gatekeeper
 * ============================================================
 *
 * This filter runs for EVERY single HTTP request before it
 * reaches any controller.
 *
 * Think of it like a security guard at the office door:
 *   - No badge (no token)    → let Spring Security decide (may block)
 *   - Invalid badge          → reject the request
 *   - Valid badge            → identify the person, let them through
 *
 * FILTER CHAIN (what happens for every request):
 *
 *   HTTP Request
 *       ↓
 *   JwtFilter.doFilterInternal()  ← runs here first
 *       ↓
 *   Read "Authorization: Bearer eyJhbG..." header
 *       ↓
 *   Extract username from JWT using JwtUtil
 *       ↓
 *   Load user from DB using UserDetailsService
 *       ↓
 *   Validate token (signature + expiry)
 *       ↓
 *   Set authentication in SecurityContext  ← "this user is logged in"
 *       ↓
 *   filterChain.doFilter() → request continues to controller
 *
 * OncePerRequestFilter → Spring calls this ONCE per request,
 * not multiple times (avoids duplicate processing).
 *
 * SecurityContextHolder → Spring's storage for "who is currently logged in".
 * Setting it here means every controller, service, and aspect
 * can access the current user via SecurityContextHolder.getContext().
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Read the Authorization header
        // Expected format: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2Vy..."
        String authHeader = request.getHeader("Authorization");

        // Step 2: If there's no token or it doesn't start with "Bearer ", skip this filter
        // The request continues — SecurityConfig will block it if the endpoint is protected
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // pass to next filter
            return;
        }

        // Step 3: Extract just the token (remove "Bearer " prefix — 7 characters)
        String jwt = authHeader.substring(7);

        try {
            // Step 4: Extract username from token
            String username = jwtUtil.extractUsername(jwt);

            // Step 5: Only process if username found AND user isn't already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 6: Load full user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Step 7: Validate the token (correct user + not expired)
                if (jwtUtil.isTokenValid(jwt, userDetails)) {

                    // Step 8: Create an authentication object
                    // This is Spring Security's way of saying "this user is authenticated"
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,           // principal (who they are)
                                    null,                  // credentials (null — already verified)
                                    userDetails.getAuthorities()  // roles (ROLE_USER, ROLE_ADMIN)
                            );

                    // Add request details (IP address, session) to the auth token
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Step 9: Store in SecurityContext → "this user is now logged in for this request"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token (expired, tampered, wrong format) → ignore and continue
            // The request will be blocked by SecurityConfig if the endpoint is protected
            System.out.println("[JWT FILTER] Invalid token: " + e.getMessage());
        }

        // Step 10: Pass request to the next filter / controller
        filterChain.doFilter(request, response);
    }
}
