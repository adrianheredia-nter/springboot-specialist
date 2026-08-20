package com.prueba.nter.modules.products.infrastructure.repository;

import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductFilterDto;
import com.prueba.nter.modules.provider.domain.ProviderEntity;
import com.prueba.nter.modules.provider.infrastructure.repository.ProviderRepository;
import com.prueba.nter.modules.users.domain.UserEntity;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Persistence tests covering the {@code @Query} methods, the Criteria API queries
 * and the unique constraint of {@link ProductEntity}.
 */
@DataJpaTest(properties = "spring.jpa.show-sql=false")
class ProductRepositoryTest {

    private static final String SHARED_NAME = "Batería Externa";

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private UserRepository userRepository;

    private ProductEntity product(String name, BigDecimal price, long providerId, long userId) {
        ProviderEntity provider = providerRepository.findById(providerId).orElseThrow();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        return ProductEntity.builder()
                .name(name)
                .description("Batería externa de 20000mAh.")
                .price(price)
                .quantity(10)
                .category("Accesorios")
                .brand("MarcaPower")
                .expirationDate(LocalDate.of(2027, 5, 1))
                .provider(provider)
                .user(user)
                .build();
    }

    @BeforeEach
    void setUp() {
        productRepository.saveAllAndFlush(List.of(
                product(SHARED_NAME, new BigDecimal("39.99"), 1L, 1L),
                product(SHARED_NAME, new BigDecimal("19.99"), 2L, 1L),
                product(SHARED_NAME, new BigDecimal("59.99"), 3L, 1L)));
    }

    @Test
    void shouldLoadTheSeedDataOfImportSql() {
        assertThat(productRepository.count()).isEqualTo(23);
        assertThat(providerRepository.count()).isEqualTo(3);
        assertThat(userRepository.count()).isEqualTo(5);
    }

    @Test
    void shouldFindUsersByEmailIgnoringCase() {
        assertThat(userRepository.findByEmailIgnoreCase("MARIA.GOMEZ@GMAIL.ES")).isPresent();
        assertThat(userRepository.existsByEmailIgnoreCase("MARIA.GOMEZ@GMAIL.ES")).isTrue();
    }

    @Test
    void shouldRejectTwoProductsWithTheSameNameForTheSameProvider() {
        ProductEntity duplicated = product(SHARED_NAME, new BigDecimal("11.11"), 1L, 1L);

        assertThatThrownBy(() -> productRepository.saveAndFlush(duplicated))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldFindProductsByName() {
        assertThat(productRepository.findByProductName("Portátil"))
                .singleElement()
                .extracting(ProductEntity::getPrice)
                .isEqualTo(new BigDecimal("999.99"));
    }

    @Test
    void shouldCountProductsByCategory() {
        assertThat(productRepository.countByProductCategory("Muebles")).isEqualTo(2);
        assertThat(productRepository.countByProductCategory("Iluminación")).isEqualTo(1);
        assertThat(productRepository.countByProductCategory("Unknown")).isZero();
    }

    @Test
    void shouldFindProductsByNameAndCategory() {
        assertThat(productRepository.findByNameAndCategory("Portátil", "Electrónica")).hasSize(1);
        assertThat(productRepository.findByNameAndCategory("Portátil", "Muebles")).isEmpty();
    }

    @Test
    void shouldReturnEveryPriceOfAProductSortedAscending() {
        List<ProductEntity> prices = productRepository.findPricesByNameOrderByPriceAsc(SHARED_NAME);

        assertThat(prices).extracting(ProductEntity::getPrice)
                .containsExactly(
                        new BigDecimal("19.99"),
                        new BigDecimal("39.99"),
                        new BigDecimal("59.99"));
    }

    @Test
    void shouldReturnTheCheapestProductOfAName() {
        assertThat(productRepository.findCheapestByName(SHARED_NAME))
                .singleElement()
                .satisfies(product -> {
                    assertThat(product.getPrice()).isEqualByComparingTo("19.99");
                    assertThat(product.getProvider().getId()).isEqualTo(2L);
                });
    }

    @Test
    void shouldSearchByOptionalNameAndCategory() {
        assertThat(productRepository.searchByNameAndCategory("portátil", null)).hasSize(1);
        assertThat(productRepository.searchByNameAndCategory("port", null)).hasSize(3);
        assertThat(productRepository.searchByNameAndCategory(null, "Muebles")).hasSize(2);
        assertThat(productRepository.searchByNameAndCategory(null, null)).hasSize(23);
    }

    @Test
    void shouldSearchByPriceLowerThan() {
        assertThat(productRepository.searchByPriceLowerThan(new BigDecimal("30.00")))
                .isNotEmpty()
                .allSatisfy(product ->
                        assertThat(product.getPrice()).isLessThan(new BigDecimal("30.00")));
    }

    @Test
    void shouldSearchByExpirationDateRangeSortedByDescendingId() {
        List<ProductEntity> products = productRepository.searchByExpirationDateRange(
                LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31));

        assertThat(products).isNotEmpty()
                .allSatisfy(product -> assertThat(product.getExpirationDate())
                        .isBetween(LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31)))
                .extracting(ProductEntity::getId)
                .isSortedAccordingTo((first, second) -> Long.compare(second, first));
    }

    @Test
    void shouldSearchByUserEmailFilteringByCategoryAndBrand() {
        assertThat(productRepository.searchByUserEmail("maria.gomez@gmail.es", null, null)).hasSize(4);
        assertThat(productRepository.searchByUserEmail("maria.gomez@gmail.es", "Electrónica", null)).hasSize(4);
        assertThat(productRepository.searchByUserEmail("maria.gomez@gmail.es", null, "MarcaAudio")).hasSize(1);
        assertThat(productRepository.searchByUserEmail("maria.gomez@gmail.es", "Muebles", null)).isEmpty();
    }

    @Test
    void shouldSearchTheProductsOfTheOldestUsers() {
        List<ProductEntity> products = productRepository.searchByOldestUsers(1);

        assertThat(products).hasSize(7)
                .allSatisfy(product -> assertThat(product.getUser().getId()).isEqualTo(1L));
    }

    @Test
    void shouldSearchAdvancedWithPaginationAndSorting() {
        ProductFilterDto filter = ProductFilterDto.builder()
                .category("Electrónica")
                .minPrice(new BigDecimal("100.00"))
                .maxPrice(new BigDecimal("1000.00"))
                .providerId(1L)
                .build();

        Page<ProductEntity> page = productRepository.searchAdvanced(
                filter, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "price")));

        assertThat(page.getTotalElements()).isPositive();
        assertThat(page.getContent()).hasSize(3)
                .extracting(ProductEntity::getPrice)
                .isSortedAccordingTo((first, second) -> second.compareTo(first));
        assertThat(page.getContent().getFirst().getPrice()).isEqualByComparingTo("999.99");
    }
}
