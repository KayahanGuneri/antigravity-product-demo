package com.antigravity.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ProductDTOs {

        public record ProductCreateRequest(
                        @NotBlank @Size(max = 255) String name,

                        @Size(max = 2000) String description,

                        @NotNull @Positive BigDecimal price,

                        @NotNull @PositiveOrZero Integer stock) {
        }

        public record ProductUpdateRequest(
                        @NotBlank @Size(max = 255) String name,

                        @Size(max = 2000) String description,

                        @NotNull @Positive BigDecimal price,

                        @NotNull @PositiveOrZero Integer stock) {
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
