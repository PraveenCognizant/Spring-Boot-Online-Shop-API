package com.example.springcore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ============================================================
 * SECURITY CONCEPT 02: User Entity — Who can log in
 * ============================================================
 *
 * This is a normal JPA @Entity, just like Product.
 * It represents a row in the "users" table in the database.
 *
 * DATABASE TABLE "users":
 * +----+----------+--------------------------------------------------------------+------------+
 * | id | username | password                                                     | role       |
 * +----+----------+--------------------------------------------------------------+------------+
 * |  1 | praveen  | $2a$10$abc...xyz  ← BCrypt hash, NOT the real password!      | ROLE_USER  |
 * |  2 | admin    | $2a$10$def...uvw  ← BCrypt hash                              | ROLE_ADMIN |
 * +----+----------+--------------------------------------------------------------+------------+
 *
 * IMPORTANT: Passwords are NEVER stored as plain text.
 * BCrypt hashes "mypassword123" into something like "$2a$10$abc123..."
 * BCrypt is a one-way function — you can't reverse it.
 * To verify: BCrypt.matches("mypassword123", storedHash) → true/false
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username must be unique — no two users with same name
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    // This stores the BCrypt hash, not the raw password
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ---- Constructors ----

    public User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}
