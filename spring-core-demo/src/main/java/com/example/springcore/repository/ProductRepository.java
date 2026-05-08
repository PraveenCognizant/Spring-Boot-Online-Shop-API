package com.example.springcore.repository;

import com.example.springcore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * CONCEPT 06: Spring Data JPA Repository — Zero Boilerplate DB Access
 * ============================================================
 *
 * JpaRepository<Product, Long> gives us ALL these methods FOR FREE:
 *
 *   save(product)           → INSERT or UPDATE
 *   findById(id)            → SELECT WHERE id = ?  (returns Optional)
 *   findAll()               → SELECT * FROM products
 *   deleteById(id)          → DELETE WHERE id = ?
 *   count()                 → SELECT COUNT(*)
 *   existsById(id)          → SELECT EXISTS(...)
 *
 * QUERY METHODS — Spring generates SQL from method names:
 *
 *   findByCategory(cat)      → SELECT * FROM products WHERE category = ?
 *   findByPriceLessThan(p)   → SELECT * FROM products WHERE price < ?
 *   findByNameContaining(n)  → SELECT * FROM products WHERE name LIKE '%n%'
 *
 * @Query — Custom JPQL (object-based) or native SQL queries
 *
 * NO implementation needed! Spring generates it at runtime.
 *
 * @Repository → marks this as a data-access bean (also converts DB exceptions
 *               to Spring's DataAccessException hierarchy)
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // --- Query Method Examples ---

    // Finds all products in a given category
    List<Product> findByCategory(String category);

    // Finds products cheaper than a given price
    List<Product> findByPriceLessThan(double price);

    // Finds products with more stock than given amount
    List<Product> findByStockGreaterThan(int stock);

    // Finds products whose name contains the search text (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Finds products in a category AND cheaper than price
    List<Product> findByCategoryAndPriceLessThan(String category, double price);

    // --- @Query Example: Custom JPQL ---
    // JPQL uses class names and field names (not table/column names)
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max ORDER BY p.price ASC")
    List<Product> findByPriceRange(@Param("min") double min, @Param("max") double max);

    // --- @Query with Native SQL ---
    @Query(value = "SELECT * FROM products WHERE stock = 0", nativeQuery = true)
    List<Product> findOutOfStockProducts();
}
