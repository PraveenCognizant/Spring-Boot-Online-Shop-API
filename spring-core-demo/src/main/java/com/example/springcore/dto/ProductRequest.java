package com.example.springcore.dto;

import jakarta.validation.constraints.*;

/**
 * ============================================================
 * CONCEPT 14: DTO (Data Transfer Object) + Bean Validation
 * ============================================================
 *
 * DTO vs Entity:
 *
 *   Entity (Product.java)      → Represents a database row, managed by JPA
 *   DTO (ProductRequest.java)  → Represents what the CLIENT sends in the request body
 *
 * WHY SEPARATE THEM?
 *   - Don't expose DB internals (like auto-generated ID) in the request
 *   - Validate incoming data BEFORE it touches your database
 *   - Different views: create vs update vs response might have different fields
 *
 * VALIDATION ANNOTATIONS:
 *   @NotNull    → field cannot be null (but can be empty string)
 *   @NotBlank   → field cannot be null, empty, or whitespace
 *   @NotEmpty   → field cannot be null or empty (allows whitespace)
 *   @Size       → string length or collection size constraints
 *   @Min/@Max   → numeric range
 *   @Positive   → > 0
 *   @PositiveOrZero → >= 0
 *   @Email      → valid email format
 *   @Pattern    → matches a regex
 *
 * These run when controller uses @Valid on the parameter.
 *
 * REQUEST JSON (from client):
 * {
 *   "name": "iPhone 15 Pro",
 *   "description": "Apple flagship phone",
 *   "price": 999.99,
 *   "stock": 50,
 *   "category": "Electronics"
 * }
 */
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotBlank(message = "Category is required")
    private String category;

    // --- Constructors ---

    public ProductRequest() {}

    public ProductRequest(String name, String description, Double price, Integer stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // --- Getters & Setters ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
