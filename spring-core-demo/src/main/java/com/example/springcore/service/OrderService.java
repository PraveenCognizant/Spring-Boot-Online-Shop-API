package com.example.springcore.service;

import com.example.springcore.model.Order;
import com.example.springcore.model.Order.OrderStatus;

import java.util.List;

/**
 * ============================================================
 * CONCEPT 10: Service Interface for Orders
 * ============================================================
 *
 * OrderServiceImpl demonstrates:
 * - Publishing custom Spring Events (@ApplicationEventPublisher)
 * - Business rule validation (stock check before ordering)
 * - Transactional operations spanning multiple repository calls
 */
public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order placeOrder(Long productId, int quantity);

    Order updateOrderStatus(Long id, OrderStatus status);

    void cancelOrder(Long id);

    List<Order> getOrdersByStatus(OrderStatus status);

    Double getTotalRevenue();
}
