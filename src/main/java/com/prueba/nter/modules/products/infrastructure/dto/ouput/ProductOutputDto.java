package com.prueba.nter.modules.products.infrastructure.dto.ouput;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Outgoing representation of a product.
 *
 * @param id             identifier of the product
 * @param name           commercial name of the product
 * @param description    free text description
 * @param price          unit price
 * @param quantity       units available in stock
 * @param category       category the product belongs to
 * @param brand          brand of the product
 * @param expirationDate date when the product expires
 * @param providerId     identifier of the provider supplying the product
 * @param providerName   name of the provider supplying the product
 * @param userId         identifier of the user owning the product
 * @param userEmail      email of the user owning the product
 */
public record ProductOutputDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String category,
        String brand,
        LocalDate expirationDate,
        Long providerId,
        String providerName,
        Long userId,
        String userEmail) {
}
