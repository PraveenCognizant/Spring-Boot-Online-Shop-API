package com.example.springcore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ============================================================
 * CONCEPT 05: JPA Relationships — @ManyToOne
 * ============================================================
 *
 * An Order belongs to ONE Product → @ManyToOne
 * Many Orders can have the same Product.
 *
 * @ManyToOne         → Creates a foreign key "product_id" in orders table
 * @JoinColumn        → Specifies the foreign key column name
 * @Enumerated        → Stores an Enum as a string in DB (not 0,1,2...)
 *
 * DATABASE TABLE "orders":
 * +----+------------+----------+--------------+-----------+---------------------+
 * | id | product_id | quantity | total_amount | status    | order_date          |
 * +----+------------+----------+--------------+-----------+---------------------+
 * |  1 |     1      |    2     |    1998.0    | CONFIRMED | 2024-01-15 10:30:00 |
 * |  2 |     2      |    3     |    268.5     | PENDING   | 2024-01-15 11:00:00 |
 * +----+------------+----------+--------------+-----------+---------------------+
 *
 * OrderStatus ENUM shows how to use Java Enums with JPA.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELATIONSHIP: Each order is for a specific product
    // @ManyToOne = Many orders can reference one product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double totalAmount;

    // @Enumerated(STRING) stores "PENDING" instead of 0
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    // ---- Enum: Order lifecycle ----
    public enum OrderStatus {
        PENDING,        // Order created but not confirmed
        CONFIRMED,      // Payment confirmed
        SHIPPED,        // Package on the way
        DELIVERED,      // Customer received it
        CANCELLED       // Order was cancelled
    }

    // ---- Constructors ----

    public Order() {}

    public Order(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalAmount = product.getPrice() * quantity;
        this.status = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", product=" + product.getName()
                + ", qty=" + quantity + ", total=" + totalAmount + ", status=" + status + "}";
    }
}
