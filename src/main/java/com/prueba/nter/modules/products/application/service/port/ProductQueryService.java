package com.prueba.nter.modules.products.application.service.port;

import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;

import java.util.List;

/**
 * Service interface exposing the product queries written with {@code @Query}.
 */
public interface ProductQueryService {

    /**
     * Finds the products stored with the given name.
     *
     * @param name the product name
     * @return the matching products
     */
    List<ProductOutputDto> findByName(String name);

    /**
     * Counts the products stored in the given category.
     *
     * @param category the category to filter by
     * @return the number of products in the category
     */
    long countByCategory(String category);

    /**
     * Finds the products stored with the given name and category.
     *
     * @param name     the product name
     * @param category the product category
     * @return the matching products
     */
    List<ProductOutputDto> findByNameAndCategory(String name, String category);

    /**
     * Retrieves the prices of a product for every provider, sorted by ascending price.
     *
     * @param name the product name
     * @return the matching products sorted by ascending price
     */
    List<ProductOutputDto> findPricesByName(String name);

    /**
     * Retrieves the cheapest product for the given name.
     *
     * @param name the product name
     * @return the cheapest product
     */
    ProductOutputDto findCheapestByName(String name);
}
