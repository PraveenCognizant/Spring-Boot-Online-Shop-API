package com.example.springcore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * ============================================================
 * CONCEPT 04: JPA Entity — Mapping Java Class to Database Table
 * ============================================================
 *
 * @Entity  → Hibernate creates a "products" table from this class
 * @Table   → Customize the table name (optional)
 * @Id     → Primary key
 * @GeneratedValue → Auto-increment ID (1, 2, 3, ...)
 *
 * VALIDATION ANNOTATIONS (from spring-boot-starter-validation):
 *   @NotBlank  → field cannot be null or whitespace
 *   @Size      → length constraints
 *   @Positive  → must be > 0
 *   @Min       → minimum value
 *   @Max       → maximum value
 *
 * These are checked when we use @Valid in the controller.
 *
 * DATABASE TABLE "products" will look like:
 * +----+----------------+-------------+-------+-------+----------+
 * | id | name           | description | price | stock | category |
 * +----+----------------+-------------+-------+-------+----------+
 * |  1 | iPhone 15 Pro  | Apple phone | 999.0 |  50   | Electronics |
 * |  2 | Running Shoes  | Nike shoes  |  89.5 |  200  | Sports   |
 * +----+----------------+-------------+-------+-------+----------+
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Positive(message = "Price must be greater than 0")
    @Column(nullable = false)
    private double price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @NotBlank(message = "Category is required")
    private String category;

    // ---- Constructors ----

    public Product() {}

    public Product(String name, String description, double price, int stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + ", stock=" + stock + "}";
    }
}
