package com.example.springcore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ============================================================
 * SECURITY CONCEPT 07: SecurityConfig — The Master Control
 * ============================================================
 *
 * This is THE most important security file.
 * It answers three questions:
 *   1. WHICH endpoints are public vs protected?
 *   2. HOW do we verify who the user is? (JWT)
 *   3. HOW do we hash passwords? (BCrypt)
 *
 * ---- WHAT HAPPENS WHEN YOU ADD spring-boot-starter-security? ----
 * Spring Boot auto-blocks ALL endpoints immediately.
 * You get a 401 Unauthorized for every request.
 * This SecurityConfig tells Spring what to allow.
 *
 * ---- KEY CONCEPTS ----
 *
 * SecurityFilterChain:
 *   A chain of filters every HTTP request passes through.
 *   Our JwtFilter is added to this chain.
 *
 * CSRF (Cross-Site Request Forgery):
 *   A web attack. We DISABLE it for REST APIs because:
 *   - REST APIs use JWT tokens (not browser cookies)
 *   - CSRF only matters when browsers auto-send cookies
 *
 * SessionCreationPolicy.STATELESS:
 *   Server does NOT create HTTP sessions.
 *   Every request must carry its JWT — server remembers nothing.
 *   This is what makes REST APIs scalable.
 *
 * BCryptPasswordEncoder:
 *   One-way hashing function for passwords.
 *   "password123" → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
 *   You can NEVER reverse it. To check: bcrypt.matches(raw, hash)
 *   The "10" is the "strength" — more = slower = harder to crack.
 *
 * ---- ENDPOINT ACCESS RULES (read top to bottom, first match wins) ----
 *
 *   POST /auth/register      → Anyone (no login needed to register)
 *   POST /auth/login         → Anyone (no login needed to log in)
 *   GET  /h2-console/**      → Anyone (dev tool, disabled in prod)
 *   GET  /actuator/**        → Anyone (monitoring)
 *   GET  /api/products/**    → Must be logged in (any role)
 *   POST /api/products/**    → Must be ADMIN
 *   PUT  /api/products/**    → Must be ADMIN
 *   DELETE /api/products/**  → Must be ADMIN
 *   /api/admin/**            → Must be ADMIN
 *   Everything else          → Must be logged in
 *
 * @EnableWebSecurity   → Activates Spring Security
 * @EnableMethodSecurity → Enables @PreAuthorize on individual methods
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * THE MAIN SECURITY RULE BOOK
     * Defines which endpoints need login and what kind of login
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (not needed for stateless JWT REST APIs)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Disable sessions — every request carries its own JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. Define URL access rules
            .authorizeHttpRequests(auth -> auth

                // --- PUBLIC endpoints (no token needed) ---
                .requestMatchers("/auth/**").permitAll()          // login & register
                .requestMatchers("/h2-console/**").permitAll()    // H2 browser (dev only)
                .requestMatchers("/actuator/**").permitAll()      // monitoring

                // --- READ products: any logged-in user ---
                .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()

                // --- WRITE products: only ADMIN ---
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ROLE_ADMIN")

                // --- Admin dashboard: only ADMIN ---
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                // --- Everything else: must be logged in ---
                .anyRequest().authenticated()
            )

            // 4. Use our custom JwtFilter BEFORE Spring's default login filter
            // This means JWT check happens before any other auth check
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            // 5. Allow H2 console to display in a frame (it uses iframes internally)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()))

            // 6. Set our custom UserDetailsService + password encoder
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    /**
     * AuthenticationProvider — Knows HOW to verify a username + password
     * Uses our UserDetailsServiceImpl to load user from DB
     * Uses BCryptPasswordEncoder to compare passwords
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);   // how to load the user
        provider.setPasswordEncoder(passwordEncoder());       // how to check the password
        return provider;
    }

    /**
     * AuthenticationManager — Used in AuthController to trigger login verification
     * Calls authenticationProvider() internally
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCryptPasswordEncoder — Hashes passwords before storing in DB
     * NEVER store plain-text passwords!
     *
     * Usage in AuthController:
     *   passwordEncoder.encode("mypassword123")
     *   → "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
