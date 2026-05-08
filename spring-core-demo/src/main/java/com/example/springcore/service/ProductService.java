package com.example.springcore.service;

import com.example.springcore.dto.ProductRequest;
import com.example.springcore.model.Product;

import java.util.List;

/**
 * ============================================================
 * CONCEPT 08: Service Interface — Program to Abstractions
 * ============================================================
 *
 * WHY USE AN INTERFACE?
 *
 *   Controller → ProductService (interface)
 *                      ↓
 *              ProductServiceImpl (real implementation)
 *
 * Benefits:
 *   1. Loose Coupling: Controller doesn't know about the implementation details
 *   2. Easy Testing: Can swap with a mock implementation in tests
 *   3. Multiple Implementations: e.g., ProductServiceImpl vs CachedProductServiceImpl
 *   4. Spring Proxy: AOP works best with interfaces (creates JDK dynamic proxies)
 *
 * The implementation is in ProductServiceImpl.java (annotated with @Service)
 */
public interface ProductService {

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product createProduct(ProductRequest request);

    Product updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    List<Product> getProductsByCategory(String category);

    List<Product> searchProducts(String name);

    List<Product> getProductsByPriceRange(double min, double max);

    List<Product> getLowStockProducts(int threshold);
}
