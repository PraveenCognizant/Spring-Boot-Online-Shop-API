package com.example.springcore.controller;

import com.example.springcore.dto.ApiResponse;
import com.example.springcore.dto.ProductRequest;
import com.example.springcore.model.Product;
import com.example.springcore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONCEPT 12: @RestController — Building REST APIs
 * ============================================================
 *
 * @RestController = @Controller + @ResponseBody
 *   - @Controller     → marks as web component, Spring processes HTTP requests
 *   - @ResponseBody   → method return value is serialized to JSON (not a view)
 *
 * @RequestMapping("/api/products") → all methods in this class start with this path
 *
 * HTTP METHOD ANNOTATIONS:
 *   @GetMapping     → HTTP GET    → Read data
 *   @PostMapping    → HTTP POST   → Create new resource
 *   @PutMapping     → HTTP PUT    → Replace entire resource
 *   @PatchMapping   → HTTP PATCH  → Partial update
 *   @DeleteMapping  → HTTP DELETE → Delete resource
 *
 * PARAMETER ANNOTATIONS:
 *   @PathVariable  → from URL: /products/{id} → Long id
 *   @RequestParam  → from query string: /products?category=Electronics
 *   @RequestBody   → from JSON body (POST/PUT)
 *   @Valid         → triggers Bean Validation on the @RequestBody
 *
 * ResponseEntity<T> → Lets us control HTTP status code + headers + body
 *   ResponseEntity.ok(data)              → 200 OK
 *   ResponseEntity.status(201).body(d)  → 201 CREATED
 *   ResponseEntity.noContent().build()  → 204 NO CONTENT
 *
 * REST API ENDPOINTS:
 *   GET    /api/products           → Get all products
 *   GET    /api/products/{id}      → Get product by ID
 *   POST   /api/products           → Create new product
 *   PUT    /api/products/{id}      → Update product
 *   DELETE /api/products/{id}      → Delete product
 *   GET    /api/products/category/{cat} → Filter by category
 *   GET    /api/products/search?name=   → Search by name
 *   GET    /api/products/price-range?min=&max= → Filter by price
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Retrieved " + products.size() + " products", products));
    }

    // GET /api/products/5
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    // POST /api/products
    // @Valid triggers validation on ProductRequest (throws 400 if invalid)
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)                          // 201 Created
                .body(ApiResponse.success("Product created successfully", product));
    }

    // PUT /api/products/5
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    // DELETE /api/products/5
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    // GET /api/products/category/Electronics
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // GET /api/products/search?name=iPhone
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(
            @RequestParam String name) {
        List<Product> products = productService.searchProducts(name);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // GET /api/products/price-range?min=100&max=500
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<Product>>> getByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        List<Product> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // GET /api/products/low-stock?threshold=10
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Product>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success("Low stock products (≤ " + threshold + ")", products));
    }
}
