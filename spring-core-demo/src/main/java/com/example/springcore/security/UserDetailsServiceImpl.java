package com.example.springcore.security;

import com.example.springcore.model.User;
import com.example.springcore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ============================================================
 * SECURITY CONCEPT 04: UserDetailsService — The Bridge
 * ============================================================
 *
 * Spring Security does NOT know about your User entity.
 * It works with its own interface called UserDetails.
 *
 * YOUR JOB: Implement UserDetailsService to tell Spring Security
 *           HOW to load a user from YOUR database.
 *
 * FLOW:
 *   Spring Security needs user info
 *         ↓
 *   Calls loadUserByUsername("praveen")
 *         ↓
 *   We query DB: userRepository.findByUsername("praveen")
 *         ↓
 *   Convert our User → Spring's UserDetails object
 *         ↓
 *   Spring Security uses it to check password, roles, etc.
 *
 * UserDetails contains:
 *   - username
 *   - password (BCrypt hash)
 *   - authorities (list of roles like "ROLE_USER")
 *   - isEnabled, isAccountNonExpired, etc. (all true by default)
 *
 * SimpleGrantedAuthority("ROLE_USER") → wraps a role string
 *   so Spring Security understands it
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Step 1: Find user in our database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        // Step 2: Convert our Role enum → Spring's GrantedAuthority
        // "ROLE_USER" becomes a SimpleGrantedAuthority that Spring understands
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        // Step 3: Build and return Spring's UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),       // username
                user.getPassword(),       // BCrypt hashed password
                List.of(authority)        // roles/authorities
        );
    }
}
