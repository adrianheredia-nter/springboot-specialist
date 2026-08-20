package com.prueba.nter.modules.products.infrastructure.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Optional filters accepted by the advanced product search.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterDto {

    /** Partial, case insensitive product name. */
    private String name;

    /** Exact category. */
    private String category;

    /** Exact brand. */
    private String brand;

    /** Minimum price, inclusive. */
    private BigDecimal minPrice;

    /** Maximum price, inclusive. */
    private BigDecimal maxPrice;

    /** Minimum expiration date, inclusive. */
    private LocalDate startDate;

    /** Maximum expiration date, inclusive. */
    private LocalDate endDate;

    /** Identifier of the provider supplying the products. */
    private Long providerId;
}
