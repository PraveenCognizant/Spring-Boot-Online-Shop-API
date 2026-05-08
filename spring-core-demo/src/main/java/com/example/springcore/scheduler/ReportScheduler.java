package com.example.springcore.scheduler;

import com.example.springcore.repository.OrderRepository;
import com.example.springcore.repository.ProductRepository;
import com.example.springcore.model.Order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 * CONCEPT 21: @Scheduled — Automated Background Tasks
 * ============================================================
 *
 * @EnableScheduling (in SpringCoreDemoApplication) activates scheduling.
 * @Scheduled marks methods to run automatically at specified times.
 *
 * THREE WAYS TO SCHEDULE:
 *
 * 1. fixedRate = 5000
 *    → Run every 5000ms (5 seconds) from application start.
 *    → Does NOT wait for previous execution to finish.
 *
 * 2. fixedDelay = 5000
 *    → Wait 5000ms AFTER the previous run finishes, then run again.
 *    → Safe when tasks might take variable time.
 *
 * 3. cron = "0 0 8 * * MON-FRI"
 *    → Cron expression: second minute hour day month weekday
 *    → "0 0 8 * * MON-FRI" = 8:00 AM every weekday
 *    → "0 * /5 * * * *"   = every 5 minutes  (note: no space before /5 in real usage)
 *    → "0 0 0 * * *"       = midnight every day
 *
 * initialDelay = 10000 → Wait 10 seconds after app startup before first run.
 *
 * REAL WORLD USES:
 *   - Send daily summary emails at 8 AM
 *   - Clear expired sessions every hour
 *   - Sync data with external APIs every 5 minutes
 *   - Generate reports at midnight
 *   - Check for low stock and alert admins
 *
 * NOTE: Scheduled methods must be void and have no parameters.
 *       @EnableScheduling is required at the application level.
 */
@Component
public class ReportScheduler {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ReportScheduler(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // Runs every 60 seconds to check inventory levels
    @Scheduled(fixedRate = 60000, initialDelay = 30000)
    public void checkLowStockAlert() {
        long outOfStockCount = productRepository.findOutOfStockProducts().size();
        if (outOfStockCount > 0) {
            System.out.println("[SCHEDULER] ⚠️  ALERT: " + outOfStockCount
                    + " product(s) are out of stock! Check inventory.");
        }
    }

    // Runs every 30 seconds — simulates a "pending orders" monitor
    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void monitorPendingOrders() {
        long pendingCount = orderRepository.countByStatus(OrderStatus.PENDING);
        System.out.println("[SCHEDULER] 🛒 Pending orders count: " + pendingCount
                + " (checked at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")");
    }

    // Cron: runs at the start of every minute (for demo — in real life use "0 0 8 * * *" for 8 AM daily)
    @Scheduled(cron = "0 * * * * *")
    public void generateMinuteReport() {
        long totalProducts = productRepository.count();
        long totalOrders   = orderRepository.count();
        Double revenue     = orderRepository.getTotalRevenue();

        System.out.println("\n[SCHEDULER] 📊 Minute Report @ "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("   Products  : " + totalProducts);
        System.out.println("   Orders    : " + totalOrders);
        System.out.println("   Revenue   : $" + (revenue != null ? revenue : 0.0) + "\n");
    }
}
