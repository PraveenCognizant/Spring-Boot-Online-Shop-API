package com.example.springcore.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * ============================================================
 * CONCEPT: Deep Dive — IoC Container & Dependency Injection
 * ============================================================
 *
 * IOC (Inversion of Control):
 *   Traditional: YOU create objects → "new EmailService()"   → tight coupling
 *   Spring IoC:  Spring creates & manages objects            → loose coupling
 *
 * TWO WAYS TO REGISTER A BEAN:
 *   1. @Component / @Service / @Repository on the class itself
 *      → Works for YOUR classes in the component-scan path
 *
 *   2. @Bean method inside a @Configuration class  ← used here
 *      → Works for third-party classes, or when you need full control
 *        over how the object is constructed
 *      → @Configuration classes are NOT component-scanned for
 *        nested @Component annotations — use @Bean methods instead
 *
 * KEY ANNOTATIONS DEMONSTRATED:
 *   @Primary   → When multiple beans of the same type exist,
 *                Spring picks this one as the default
 *   @Qualifier → Override the default — pick a specific bean by name
 *   @Scope     → Controls how many instances Spring creates
 *
 * BEAN SCOPES:
 *   singleton  → ONE instance shared everywhere (DEFAULT)
 *   prototype  → NEW instance every time it is injected
 *   request    → New instance per HTTP request (web apps)
 *   session    → New instance per user session (web apps)
 *
 * THREE INJECTION STYLES (shown in @Bean factory methods below):
 *   1. Constructor injection  → RECOMMENDED (immutable, testable)
 *   2. Setter injection       → for optional dependencies
 *   3. Field injection        → simplest, but avoids in production
 */
@Configuration
public class DIConceptsDemo {

    // =========================================================
    // Interface + Two Implementations (plain Java — no annotations)
    // They become Spring beans only because @Bean methods return them.
    // =========================================================

    public interface NotificationService {
        void send(String message);
    }

    public static class EmailNotificationService implements NotificationService {
        @Override
        public void send(String message) {
            System.out.println("[EMAIL] Sending: " + message);
        }
    }

    public static class SmsNotificationService implements NotificationService {
        @Override
        public void send(String message) {
            System.out.println("[SMS] Sending: " + message);
        }
    }

    // =========================================================
    // Register both NotificationService implementations as beans
    // =========================================================

    // @Primary → injected by default when type is NotificationService
    @Bean
    @Primary
    public NotificationService emailNotificationService() {
        return new EmailNotificationService();
    }

    // Named "smsNotificationService" → only injected when @Qualifier is used
    @Bean("smsNotificationService")
    public NotificationService smsNotificationService() {
        return new SmsNotificationService();
    }

    // =========================================================
    // Scope demonstration
    // =========================================================

    public static class CartService {
        private static int instanceCount = 0;
        public CartService() {
            System.out.println("[SCOPE] CartService (singleton) instance #" + (++instanceCount) + " created");
        }
    }

    public static class RequestContext {
        private static int instanceCount = 0;
        public RequestContext() {
            System.out.println("[SCOPE] RequestContext (prototype) instance #" + (++instanceCount) + " created");
        }
    }

    @Bean
    @Scope("singleton")   // one shared instance (default — explicitly shown here)
    public CartService cartService() {
        return new CartService();
    }

    @Bean
    @Scope("prototype")   // new instance every time this bean is requested
    public RequestContext requestContext() {
        return new RequestContext();
    }

    // =========================================================
    // Injection style demonstration via @Bean factory methods
    // =========================================================

    // ---- STYLE 1: Constructor Injection (RECOMMENDED) ----
    // Spring sees the parameter → injects the @Primary bean automatically
    public static class OrderProcessor {
        private final NotificationService notificationService; // final = immutable

        public OrderProcessor(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        public void process(String orderId) {
            notificationService.send("Order " + orderId + " is being processed");
        }
    }

    @Bean
    public OrderProcessor orderProcessor(NotificationService notificationService) {
        // Spring injects @Primary (emailNotificationService) automatically
        return new OrderProcessor(notificationService);
    }

    // ---- STYLE 2: Setter Injection ----
    // Used when a dependency is optional or can change after construction
    public static class ReportGenerator {
        private NotificationService notificationService;

        public void setNotificationService(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        public void sendReport(String report) {
            if (notificationService != null) {
                notificationService.send(report);
            }
        }
    }

    @Bean
    public ReportGenerator reportGenerator(NotificationService notificationService) {
        ReportGenerator generator = new ReportGenerator();
        generator.setNotificationService(notificationService); // setter injection
        return generator;
    }

    // ---- STYLE 3: @Qualifier — explicitly pick sms, not the @Primary email ----
    public static class InventoryChecker {
        private final NotificationService notificationService;

        public InventoryChecker(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        public void alertLowStock(String productName) {
            notificationService.send("LOW STOCK alert: " + productName);
        }
    }

    @Bean
    public InventoryChecker inventoryChecker(
            @Qualifier("smsNotificationService") NotificationService smsService) {
        // @Qualifier overrides @Primary — explicitly picks smsNotificationService
        return new InventoryChecker(smsService);
    }

    // ---- Custom named bean ----
    @Bean(name = "appGreeting")
    public String greeting() {
        return "Welcome to the Spring Boot Online Shop!";
    }
}
