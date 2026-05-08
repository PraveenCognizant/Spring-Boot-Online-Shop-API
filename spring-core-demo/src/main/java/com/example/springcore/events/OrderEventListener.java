package com.example.springcore.events;

import com.example.springcore.model.Order;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 * CONCEPT 20: @EventListener — Reacting to Spring Events
 * ============================================================
 *
 * @EventListener → Spring calls this method whenever the matching
 *                  event type is published.
 *
 * Spring automatically infers the event type from the method parameter.
 *   onOrderPlaced(OrderPlacedEvent event) → listens for OrderPlacedEvent
 *
 * MULTIPLE LISTENERS: You can have many methods listening to the same event.
 * Each runs independently.
 *
 * BUILT-IN SPRING EVENTS you can listen to:
 *   ContextRefreshedEvent  → ApplicationContext was initialized/refreshed
 *   ContextStartedEvent    → ApplicationContext.start() was called
 *   ContextStoppedEvent    → ApplicationContext.stop() was called
 *   ContextClosedEvent     → Application is shutting down
 *
 * @Async → runs the listener in a separate thread (non-blocking).
 *          The service doesn't wait for the email to send.
 *          Requires @EnableAsync on a @Configuration class.
 *
 * In a real app, you would:
 *   - Inject JavaMailSender and actually send email
 *   - Inject an SMS service
 *   - Publish to a message queue (Kafka/RabbitMQ)
 */
@Component
public class OrderEventListener {

    // Listens for our custom OrderPlacedEvent
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        Order order = event.getOrder();
        System.out.println("\n📧 [EVENT] Order Confirmation Email Sent!");
        System.out.println("   Order ID    : #" + order.getId());
        System.out.println("   Product     : " + order.getProduct().getName());
        System.out.println("   Quantity    : " + order.getQuantity());
        System.out.println("   Total       : $" + order.getTotalAmount());
        System.out.println("   Status      : " + order.getStatus());
        System.out.println("   (In a real app: JavaMailSender.send() would be called here)\n");
    }

    // Listens for Spring's built-in ContextRefreshedEvent
    // Runs once when the application context is fully loaded
    @EventListener
    public void onApplicationStartup(ContextRefreshedEvent event) {
        System.out.println("[EVENT] Spring ApplicationContext is ready! "
                + "All beans are loaded and the app is live.");
    }
}
