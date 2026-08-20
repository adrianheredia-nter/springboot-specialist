package com.prueba.nter.modules.products.infrastructure.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Incoming representation of a product, matching the structure of {@code Products.json}.
 *
 * @param name           commercial name of the product
 * @param description    free text description
 * @param price          unit price
 * @param quantity       units available in stock
 * @param category       category the product belongs to
 * @param brand          brand of the product
 * @param expirationDate date when the product expires
 * @param providerId     identifier of the provider supplying the product
 * @param userId         identifier of the user owning the product
 */
public record ProductInputDto(
        @NotBlank String name,
        @Size(max = 500) String description,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull @PositiveOrZero Integer quantity,
        @NotBlank String category,
        String brand,
        @NotNull LocalDate expirationDate,
        @NotNull Long providerId,
        @NotNull Long userId) {
}
