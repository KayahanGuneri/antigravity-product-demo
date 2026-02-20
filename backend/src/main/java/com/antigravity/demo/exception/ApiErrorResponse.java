package com.antigravity.demo.exception;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response for all API exceptions.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        List<FieldViolation> fieldErrors) {
    public record FieldViolation(String field, String message) {
    }
}
