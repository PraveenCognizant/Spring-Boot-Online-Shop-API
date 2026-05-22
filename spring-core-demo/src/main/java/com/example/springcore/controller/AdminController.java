package com.example.springcore.controller;

import com.example.springcore.dto.ApiResponse;
import com.example.springcore.repository.OrderRepository;
import com.example.springcore.repository.ProductRepository;
import com.example.springcore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ============================================================
 * SECURITY CONCEPT 09: Role-Based Access Control
 * ============================================================
 *
 * TWO ways to restrict access by role:
 *
 * WAY 1 — In SecurityConfig (URL-level):
 *   .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
 *   → Entire path is blocked for non-admins before reaching the controller
 *
 * WAY 2 — @PreAuthorize on the method (method-level):
 *   @PreAuthorize("hasRole('ADMIN')")
 *   → Fine-grained control: protect one method in an otherwise open controller
 *   → Requires @EnableMethodSecurity in SecurityConfig
 *
 * DIFFERENCE between hasRole() and hasAuthority():
 *   hasRole('ADMIN')         → checks for "ROLE_ADMIN" (auto-adds the "ROLE_" prefix)
 *   hasAuthority('ROLE_ADMIN') → checks exactly for "ROLE_ADMIN" (no prefix added)
 *
 * @AuthenticationPrincipal UserDetails currentUser
 *   → Spring injects the currently logged-in user's details directly into the method.
 *   → No need to call SecurityContextHolder manually.
 *   → Works because JwtFilter set the Authentication in the SecurityContext.
 *
 * If a ROLE_USER tries to call any endpoint here:
 *   → 403 Forbidden (not 401 — they ARE authenticated, just not AUTHORIZED)
 *
 * 401 Unauthorized = "Who are you?" (not logged in)
 * 403 Forbidden    = "I know who you are, but you can't do this" (wrong role)
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")   // applies to ALL methods in this controller
public class AdminController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(ProductRepository productRepository,
                           OrderRepository orderRepository,
                           UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // GET /api/admin/dashboard
    // Only ROLE_ADMIN can reach this
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard(
            @AuthenticationPrincipal UserDetails currentUser) {

        // @AuthenticationPrincipal gives us the logged-in user without any extra code
        return ApiResponse.success("Admin Dashboard", Map.of(
                "loggedInAs",   currentUser.getUsername(),
                "role",         currentUser.getAuthorities().iterator().next().getAuthority(),
                "totalProducts", productRepository.count(),
                "totalOrders",   orderRepository.count(),
                "totalUsers",    userRepository.count()
        ));
    }

    // GET /api/admin/users
    // Returns all registered users — admin only
    @GetMapping("/users")
    public ApiResponse<Object> getAllUsers() {
        return ApiResponse.success(
                userRepository.findAll().stream()
                        .map(u -> Map.of(
                                "id",       u.getId(),
                                "username", u.getUsername(),
                                "role",     u.getRole().name()
                        ))
                        .toList()
        );
    }
}
