package com.example.springcore;

import com.example.springcore.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ============================================================
 * CONCEPT: Integration Testing with @SpringBootTest + MockMvc
 * ============================================================
 *
 * Integration Test = tests the WHOLE stack together:
 *   HTTP request → Controller → Service → Repository → H2 DB
 *
 * @SpringBootTest → Loads the FULL ApplicationContext (all beans).
 *                   Slower than unit tests but tests real behavior.
 *
 * @AutoConfigureMockMvc → Creates a MockMvc bean (simulates HTTP).
 *
 * @ActiveProfiles("test") → Uses application-test.properties if present,
 *                           else falls back to application.properties.
 *                           Ensures we use H2, not a real DB.
 *
 * @Transactional → Each test runs in a transaction that's ROLLED BACK
 *                  after the test — DB stays clean between tests.
 *
 * MockMvc → Simulates HTTP requests without starting a real server.
 *   perform(get("/api/products"))        → simulate GET
 *   perform(post(...).content(json))     → simulate POST
 *   .andExpect(status().isOk())          → assert HTTP 200
 *   .andExpect(jsonPath("$.success").value(true))  → assert JSON field
 *
 * jsonPath Cheat Sheet:
 *   $.success          → top-level field "success"
 *   $.data[0].name     → first element's "name" in "data" array
 *   $.data.length()    → array length
 */
@SuppressWarnings("null")  // MockMvc/Hamcrest matchers safely return null for @NonNull params
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
// @WithMockUser simulates a logged-in user for every test in this class.
// Without this, all requests would return 401 because endpoints are now secured.
// roles={"ADMIN"} needed because POST/PUT/DELETE products require ROLE_ADMIN.
@WithMockUser(username = "testuser", roles = {"ADMIN"})
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // Jackson — converts objects to/from JSON

    @Test
    @DisplayName("GET /api/products returns 200 and a list of products")
    void getAllProducts_returnsOkWithList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /api/products with valid body creates product and returns 201")
    void createProduct_validRequest_returns201() throws Exception {
        ProductRequest request = new ProductRequest(
                "Test Product", "A test item", 49.99, 10, "Testing");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())          // 201
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(49.99))
                .andExpect(jsonPath("$.data.id").isNumber()); // ID auto-generated
    }

    @Test
    @DisplayName("POST /api/products with blank name returns 400 validation error")
    void createProduct_blankName_returns400() throws Exception {
        ProductRequest request = new ProductRequest("", "desc", 10.0, 5, "Cat");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())       // 400
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/products/9999 returns 404 not found")
    void getProductById_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound())         // 404
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("9999")));
    }

    @Test
    @DisplayName("GET /api/products/search?name=x returns filtered results")
    void searchProducts_returnsMatchingProducts() throws Exception {
        // First create a product to search for
        ProductRequest request = new ProductRequest("SearchMe Item", "unique", 25.0, 5, "Test");
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Now search for it
        mockMvc.perform(get("/api/products/search").param("name", "SearchMe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].name").value(containsString("SearchMe")));
    }
}
