package com.example.springcore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ============================================================
 * Custom Exception: Bad Request (400)
 * ============================================================
 *
 * Used when the client's request is valid JSON but breaks
 * a BUSINESS RULE (not a validation error).
 *
 * Examples:
 *   - Ordering more stock than available
 *   - Cancelling a shipped order
 *   - Setting an invalid status transition
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
