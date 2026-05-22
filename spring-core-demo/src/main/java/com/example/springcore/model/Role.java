package com.example.springcore.model;

/**
 * ============================================================
 * SECURITY CONCEPT 01: Roles — What a user is ALLOWED to do
 * ============================================================
 *
 * A Role defines a USER's PERMISSION LEVEL in the system.
 *
 * ROLE_USER  → Regular user  → Can browse products, place orders
 * ROLE_ADMIN → Admin user    → Can also create/delete products, see admin dashboard
 *
 * Spring Security convention: role names MUST start with "ROLE_"
 * When you write hasRole("ADMIN"), Spring automatically checks for "ROLE_ADMIN"
 *
 * Real-world examples:
 *   E-commerce: ROLE_CUSTOMER, ROLE_SELLER, ROLE_ADMIN
 *   Hospital:   ROLE_PATIENT, ROLE_DOCTOR, ROLE_ADMIN
 *   School:     ROLE_STUDENT, ROLE_TEACHER, ROLE_PRINCIPAL
 */
public enum Role {
    ROLE_USER,    // normal registered user
    ROLE_ADMIN    // administrator with full access
}
