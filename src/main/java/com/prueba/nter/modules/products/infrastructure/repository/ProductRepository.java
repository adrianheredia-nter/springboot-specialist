package com.prueba.nter.modules.products.infrastructure.repository;

import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.repository.custom.ProductCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ProductEntity.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, ProductCustomRepository {

    /**
     * Checks whether the given provider already supplies a product with that name.
     *
     * @param name       the product name
     * @param providerId the provider identifier
     * @return {@code true} if the combination already exists
     */
    boolean existsByNameAndProviderId(String name, Long providerId);

    /**
     * Finds every product sharing the given name.
     *
     * @param name the product name
     * @return the matching products
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.name = :name")
    List<ProductEntity> findByProductName(@Param("name") String name);

    /**
     * Counts the products stored in the given category.
     *
     * @param category the category to filter by
     * @return the number of products in the category
     */
    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.category = :category")
    long countByProductCategory(@Param("category") String category);

    /**
     * Finds every product matching both name and category.
     *
     * @param name     the product name
     * @param category the product category
     * @return the matching products
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.name = :name AND p.category = :category")
    List<ProductEntity> findByNameAndCategory(@Param("name") String name, @Param("category") String category);

    /**
     * Retrieves the price of a product for every provider, sorted by ascending price.
     *
     * @param name the product name
     * @return the matching products sorted by ascending price
     */
    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.provider WHERE p.name = :name ORDER BY p.price ASC")
    List<ProductEntity> findPricesByNameOrderByPriceAsc(@Param("name") String name);

    /**
     * Retrieves the cheapest products for a given name using a subquery with aggregation.
     *
     * @param name the product name
     * @return the products holding the minimum price for that name
     */
    @Query("""
            SELECT p FROM ProductEntity p
            WHERE p.name = :name
              AND p.price = (SELECT MIN(cheapest.price) FROM ProductEntity cheapest WHERE cheapest.name = :name)
            """)
    List<ProductEntity> findCheapestByName(@Param("name") String name);
}
