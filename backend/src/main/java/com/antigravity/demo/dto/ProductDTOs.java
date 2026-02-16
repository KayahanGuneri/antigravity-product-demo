package com.antigravity.demo.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ProductDTOs {

    public record ProductCreateRequest(
            String name,
            String description,
            BigDecimal price,
            Integer stock) {
    }

    public record ProductUpdateRequest(
            String name,
            String description,
            BigDecimal price,
            Integer stock) {
    }

    public record ProductResponse(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            Instant createdAt) {
    }
}
