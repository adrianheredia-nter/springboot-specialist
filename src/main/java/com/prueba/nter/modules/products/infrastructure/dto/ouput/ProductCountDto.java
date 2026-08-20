package com.prueba.nter.modules.products.infrastructure.dto.ouput;

/**
 * Number of products belonging to a category.
 *
 * @param category the category used as filter
 * @param total    the number of products found
 */
public record ProductCountDto(String category, long total) {
}
