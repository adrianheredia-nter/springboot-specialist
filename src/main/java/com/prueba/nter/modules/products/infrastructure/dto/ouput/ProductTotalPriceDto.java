package com.prueba.nter.modules.products.infrastructure.dto.ouput;

import java.math.BigDecimal;

/**
 * Total price of the products read from an uploaded file.
 *
 * @param products   number of products read from the file
 * @param totalPrice sum of the prices of those products
 */
public record ProductTotalPriceDto(int products, BigDecimal totalPrice) {
}
