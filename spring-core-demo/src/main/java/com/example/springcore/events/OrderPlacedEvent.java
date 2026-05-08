package com.example.springcore.events;

import com.example.springcore.model.Order;
import org.springframework.context.ApplicationEvent;

/**
 * ============================================================
 * CONCEPT 19: Custom Spring Application Events
 * ============================================================
 *
 * Spring's Event System = built-in Observer Pattern.
 *
 * WHY USE EVENTS?
 *   When an order is placed, you might want to:
 *   - Send a confirmation email
 *   - Update inventory metrics
 *   - Notify a warehouse system
 *   - Push a mobile notification
 *
 *   WITHOUT events: OrderServiceImpl calls EmailService, MetricsService,
 *                   WarehouseService... tight coupling, hard to extend.
 *
 *   WITH events: OrderServiceImpl just publishes ONE event.
 *               Multiple listeners handle their own responsibilities.
 *               Adding new behavior = add a new listener, zero changes to service.
 *
 * FLOW:
 *   OrderServiceImpl → eventPublisher.publishEvent(new OrderPlacedEvent(...))
 *                          ↓
 *   OrderEventListener.onOrderPlaced()    ← runs automatically
 *
 * ApplicationEvent → base class for all Spring events.
 * 'source' parameter = the object that published the event (the service).
 */
public class OrderPlacedEvent extends ApplicationEvent {

    private final Order order;

    public OrderPlacedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
