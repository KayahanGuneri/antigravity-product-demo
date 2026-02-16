package com.antigravity.demo.exception;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String message,
        Instant timestamp) {
}
