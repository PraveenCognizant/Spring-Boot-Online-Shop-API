package com.example.springcore.controller;

import com.example.springcore.dto.ApiResponse;
import com.example.springcore.model.Order;
import com.example.springcore.model.Order.OrderStatus;
import com.example.springcore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CONCEPT 13: Order Controller — @RequestParam + @RequestBody Map
 * ============================================================
 *
 * Demonstrates:
 * - @RequestParam for simple parameters
 * - Accepting a JSON Map for simple request bodies
 * - Enum in path variables (@PathVariable with Enum type)
 *
 * REST API:
 *   GET    /api/orders              → All orders
 *   GET    /api/orders/{id}         → Order by ID
 *   POST   /api/orders              → Place new order
 *   PATCH  /api/orders/{id}/status  → Update order status
 *   DELETE /api/orders/{id}/cancel  → Cancel order
 *   GET    /api/orders/status/{s}   → Orders by status
 *   GET    /api/orders/revenue      → Total revenue
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/orders
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders()));
    }

    // GET /api/orders/3
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
    }

    // POST /api/orders
    // Body: { "productId": 1, "quantity": 2 }
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> placeOrder(@RequestBody Map<String, Integer> body) {
        Long productId = body.get("productId").longValue();
        int quantity = body.get("quantity");
        Order order = orderService.placeOrder(productId, quantity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully!", order));
    }

    // PATCH /api/orders/3/status
    // Body: { "status": "CONFIRMED" }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        OrderStatus status = OrderStatus.valueOf(body.get("status").toUpperCase());
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    // DELETE /api/orders/3/cancel
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled and stock restored", null));
    }

    // GET /api/orders/status/PENDING
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByStatus(status)));
    }

    // GET /api/orders/revenue
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getRevenue() {
        Double revenue = orderService.getTotalRevenue();
        return ResponseEntity.ok(ApiResponse.success(Map.of("totalRevenue", revenue)));
    }
}
