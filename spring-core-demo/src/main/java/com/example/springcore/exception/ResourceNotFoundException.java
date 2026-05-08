package com.example.springcore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ============================================================
 * CONCEPT 16: Custom Exception Classes
 * ============================================================
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND) → when this exception is thrown,
 * Spring automatically responds with HTTP 404 NOT FOUND.
 *
 * We extend RuntimeException so we don't need "throws" declarations everywhere.
 *
 * USAGE in service:
 *   throw new ResourceNotFoundException("Product", "id", 999L);
 *
 * OUTPUT to client (handled by GlobalExceptionHandler):
 * {
 *   "success": false,
 *   "message": "Product not found with id: 999",
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " not found with " + fieldName + ": " + fieldValue);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName() { return fieldName; }
    public Object getFieldValue() { return fieldValue; }
}
