package com.example.springcore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * ============================================================
 * CONCEPT 15: Standard API Response Wrapper
 * ============================================================
 *
 * Instead of returning raw objects or raw error strings, we wrap
 * every response in a consistent envelope. This is API best practice.
 *
 * SUCCESS response:
 * {
 *   "success": true,
 *   "message": "Product created successfully",
 *   "data": { "id": 1, "name": "iPhone 15 Pro", ... },
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 *
 * ERROR response:
 * {
 *   "success": false,
 *   "message": "Product not found with id: 999",
 *   "data": null,
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 *
 * @JsonInclude(NON_NULL) → fields with null value are NOT included in JSON output
 * This is a Jackson annotation (Spring uses Jackson for JSON).
 *
 * Generic type <T> means data can be: Product, Order, List<Product>, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // ---- Static Factory Methods (clean way to create responses) ----

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        response.timestamp = LocalDateTime.now();
        return response;
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.timestamp = LocalDateTime.now();
        return response;
    }

    // ---- Getters ----

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
