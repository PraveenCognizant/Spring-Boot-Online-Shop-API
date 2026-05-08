package com.example.springcore.exception;

import com.example.springcore.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * CONCEPT 17: @RestControllerAdvice — Centralized Error Handling
 * ============================================================
 *
 * Without this: every controller method would need try-catch blocks.
 * With this: exceptions "bubble up" and are caught here in ONE place.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   Intercepts exceptions from ALL controllers in the application.
 *
 * @ExceptionHandler(XyzException.class)
 *   Runs this method when that specific exception is thrown anywhere.
 *
 * EXCEPTION HIERARCHY handled here:
 *
 *   ResourceNotFoundException  → 404 Not Found
 *   BadRequestException        → 400 Bad Request
 *   MethodArgumentNotValidException → 400 Bad Request (validation failures)
 *   IllegalArgumentException   → 400 Bad Request
 *   Exception (catch-all)      → 500 Internal Server Error
 *
 * FLOW:
 *   Controller → Service throws ResourceNotFoundException
 *              → Spring looks for @ExceptionHandler(ResourceNotFoundException.class)
 *              → Calls handleResourceNotFound()
 *              → Returns 404 JSON response to client
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- Handle 404 Not Found ---
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // --- Handle 400 Bad Request (business rule violations) ---
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // --- Handle 400 Validation Errors (@Valid failures) ---
    // Spring throws this when @Valid fails on @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Collect all field errors into a map: { "name": "Name is required", "price": "Price must be > 0" }
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed: " + errors));
    }

    // --- Handle 400 IllegalArgument (e.g., invalid enum value) ---
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid value: " + ex.getMessage()));
    }

    // --- Catch-all: Handle any unexpected exception with 500 ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        // In real apps: log the full stack trace here with a logger
        System.err.println("Unexpected error: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong. Please try again."));
    }
}
