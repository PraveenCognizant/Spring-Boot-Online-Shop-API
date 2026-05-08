package com.example.springcore.service.impl;

import com.example.springcore.dto.ProductRequest;
import com.example.springcore.exception.ResourceNotFoundException;
import com.example.springcore.model.Product;
import com.example.springcore.repository.ProductRepository;
import com.example.springcore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * CONCEPT 09: @Service + @Transactional — Business Logic Layer
 * ============================================================
 *
 * @Service → Specialization of @Component for business logic.
 *            Spring creates ONE instance (singleton) and manages it.
 *            When ProductController says @Autowired ProductService,
 *            Spring injects THIS class (because it implements ProductService).
 *
 * DEPENDENCY INJECTION via @Autowired:
 *   Spring sees "I need a ProductRepository" → finds the bean it created
 *   → injects it. We never write "new ProductRepository()".
 *
 * @Transactional → Wraps method in a database transaction:
 *   - If method succeeds → COMMIT (saves to DB)
 *   - If exception thrown → ROLLBACK (nothing saved)
 *
 *   readOnly = true → tells Hibernate no changes expected (performance boost)
 *
 * CONSTRUCTOR INJECTION vs FIELD INJECTION:
 *   - Constructor injection (recommended): testable, immutable, clear dependencies
 *   - Field injection (@Autowired on field): simpler but harder to test
 *   Below we use constructor injection.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // Constructor Injection — Spring sees one constructor, auto-injects dependencies
    // The @Autowired is optional when there's only one constructor (Spring Boot 4.3+)
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)     // Read-only: no DB changes, Hibernate optimizes
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        // findById returns Optional<Product>
        // orElseThrow → if empty, throw our custom exception
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    public Product createProduct(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                request.getCategory()
        );
        return productRepository.save(product);   // INSERT into DB
    }

    @Override
    public Product updateProduct(Long id, ProductRequest request) {
        Product existing = getProductById(id);    // Throws 404 if not found

        // Update fields
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategory(request.getCategory());

        return productRepository.save(existing);  // UPDATE in DB
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);     // Throws 404 if not found
        productRepository.delete(product);         // DELETE from DB
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(double min, double max) {
        return productRepository.findByPriceRange(min, max);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        // Reuse repository method: stock less than threshold means "low stock"
        return productRepository.findAll().stream()
                .filter(p -> p.getStock() > 0 && p.getStock() <= threshold)
                .toList();
    }
}
