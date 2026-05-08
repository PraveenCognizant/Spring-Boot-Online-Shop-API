package com.example.springcore.repository;

import com.example.springcore.model.Order;
import com.example.springcore.model.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * CONCEPT 07: More Repository Features
 * ============================================================
 *
 * This demonstrates querying by:
 * - Enum fields (OrderStatus)
 * - Nested object fields (product.id via "Product_Id")
 * - Custom aggregation with @Query
 *
 * Spring Data JPA naming rules:
 *   findBy[FieldName][Condition]
 *   Fields of related entities: findByProduct_Id → joins orders → products
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // All orders with a specific status (uses Enum)
    List<Order> findByStatus(OrderStatus status);

    // All orders for a specific product (joins via FK)
    List<Order> findByProduct_Id(Long productId);

    // Orders sorted by date (newest first)
    List<Order> findAllByOrderByOrderDateDesc();

    // Total revenue from confirmed orders
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'CONFIRMED'")
    Double getTotalRevenue();

    // Count orders by status
    long countByStatus(OrderStatus status);
}
