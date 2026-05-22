package com.example.springcore.repository;

import com.example.springcore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================
 * SECURITY CONCEPT 03: UserRepository
 * ============================================================
 *
 * Just like ProductRepository, but for users.
 *
 * findByUsername() is used in two places:
 *   1. During LOGIN  → check if user exists, then verify password
 *   2. In JwtFilter  → after reading username from JWT token,
 *                       load the full user from DB to set in SecurityContext
 *
 * existsByUsername() is used during REGISTER
 *   → prevent duplicate usernames
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by their username (returns empty if not found)
    Optional<User> findByUsername(String username);

    // Returns true if a user with this username already exists
    boolean existsByUsername(String username);
}
