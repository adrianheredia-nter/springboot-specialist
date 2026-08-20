package com.prueba.nter.modules.products.infrastructure.repository.custom;

import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Product queries built dynamically with the Criteria API.
 */
public interface ProductCustomRepository {

    /**
     * Searches products by the optional name and category query parameters.
     *
     * @param name     the product name, optional
     * @param category the product category, optional
     * @return the matching products
     */
    List<ProductEntity> searchByNameAndCategory(String name, String category);

    /**
     * Searches products cheaper than the given price.
     *
     * @param price the exclusive upper price bound
     * @return the matching products
     */
    List<ProductEntity> searchByPriceLowerThan(BigDecimal price);

    /**
     * Searches products expiring within the given date range, sorted by descending identifier.
     *
     * @param startDate the lower bound of the expiration date, inclusive
     * @param endDate   the upper bound of the expiration date, inclusive
     * @return the matching products
     */
    List<ProductEntity> searchByExpirationDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Searches the products owned by the user with the given email, optionally
     * narrowing the result by category and brand.
     *
     * @param email    the owner email
     * @param category the product category, optional
     * @param brand    the product brand, optional
     * @return the matching products
     */
    List<ProductEntity> searchByUserEmail(String email, String category, String brand);

    /**
     * Searches the products owned by the N oldest users of the system.
     *
     * @param limit the number of users to consider
     * @return the matching products
     */
    List<ProductEntity> searchByOldestUsers(int limit);

    /**
     * Performs an advanced search combining the given optional filters with
     * pagination and sorting.
     *
     * @param filter   the optional filters
     * @param pageable the pagination and sorting information
     * @return a page of matching products
     */
    Page<ProductEntity> searchAdvanced(ProductFilterDto filter, Pageable pageable);
}
