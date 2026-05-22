package com.example.springcore.service.impl;

import com.example.springcore.events.OrderPlacedEvent;
import com.example.springcore.exception.BadRequestException;
import com.example.springcore.exception.ResourceNotFoundException;
import com.example.springcore.model.Order;
import com.example.springcore.model.Order.OrderStatus;
import com.example.springcore.model.Product;
import com.example.springcore.repository.OrderRepository;
import com.example.springcore.repository.ProductRepository;
import com.example.springcore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * CONCEPT 11: Business Logic + Spring Events
 * ============================================================
 *
 * ApplicationEventPublisher → Publishes custom events to all listeners.
 *
 * Event Flow for placing an order:
 *   1. Client calls POST /api/orders
 *   2. OrderController → OrderService.placeOrder()
 *   3. Service checks stock → creates Order → saves to DB
 *   4. Service publishes OrderPlacedEvent
 *   5. OrderEventListener receives the event → sends "email" (logs it)
 *
 * This is the Observer Pattern built into Spring.
 * The service doesn't know WHO handles the event — loose coupling!
 *
 * @Transactional here wraps BOTH:
 *   - productRepository.save() (stock update)
 *   - orderRepository.save() (new order)
 *   If either fails, BOTH are rolled back — data stays consistent.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }



    @Override
    public Order placeOrder(Long productId, int quantity) {
        // BUSINESS RULE 1: Product must exist
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // BUSINESS RULE 2: Quantity must be positive
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be at least 1");
        }

        // BUSINESS RULE 3: Sufficient stock must be available
        if (product.getStock() < quantity) {
            throw new BadRequestException(
                "Insufficient stock. Available: " + product.getStock() + ", Requested: " + quantity
            );
        }

        // Deduct stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // Create order
        Order order = new Order(product, quantity);
        Order savedOrder = orderRepository.save(order);

        // Publish event — listeners will handle notifications, emails, etc.
        // This decouples the order logic from notification logic
        eventPublisher.publishEvent(new OrderPlacedEvent(this, savedOrder));

        return savedOrder;
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = getOrderById(id);

        // BUSINESS RULE: Can't change status of delivered orders
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot change status of a delivered order");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel order that is already shipped or delivered");
        }

        // Restore stock when cancelled
        Product product = order.getProduct();
        product.setStock(product.getStock() + order.getQuantity());
        productRepository.save(product);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalRevenue() {
        Double revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }
}
