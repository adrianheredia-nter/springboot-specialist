package com.prueba.nter.modules.products.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.repository.ProductRepository;
import com.prueba.nter.modules.provider.infrastructure.repository.ProviderRepository;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link ProductQueryController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductQueryControllerIntegrationTest {

    private static final String SHARED_NAME = "Batería Externa";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private UserRepository userRepository;

    private ProductEntity product(BigDecimal price, long providerId) {
        return ProductEntity.builder()
                .name(SHARED_NAME)
                .description("Batería externa de 20000mAh.")
                .price(price)
                .quantity(10)
                .category("Accesorios")
                .brand("MarcaPower")
                .expirationDate(LocalDate.of(2027, 5, 1))
                .provider(providerRepository.findById(providerId).orElseThrow())
                .user(userRepository.findById(1L).orElseThrow())
                .build();
    }

    @BeforeEach
    void setUp() {
        productRepository.saveAllAndFlush(List.of(
                product(new BigDecimal("39.99"), 1L),
                product(new BigDecimal("19.99"), 2L),
                product(new BigDecimal("59.99"), 3L)));
    }

    @Test
    void shouldFindProductsByName() throws Exception {
        mockMvc.perform(get(Constants.QUERY_PRODUCTS_PATH + "/by-name").param("name", "Portátil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price").value(999.99));
    }

    @Test
    void shouldReturnNotFoundForAnUnknownName() throws Exception {
        mockMvc.perform(get(Constants.QUERY_PRODUCTS_PATH + "/by-name").param("name", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with name Unknown not found!"));
    }

    @Test
    void shouldReturnBadRequestForABlankName() throws Exception {
        mockMvc.perform(get(Constants.QUERY_PRODUCTS_PATH + "/by-name").param("name", " "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCountProductsByCategory() throws Exception {
        mockMvc.perform(get(Constants.QUERY_PRODUCTS_PATH + "/count-by-category")
                        .param("category", "Muebles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Muebles"))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void shouldFindProductsByNameAndCategory() throws Exception {
        mockMvc.perform(get(Constants.QUERY_PRODUCTS_PATH + "/by-name-and-category")
                        .param("name", "Portátil")
                        .param("category", "Electrónica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnEveryPriceOfAProductSortedAscending() throws Exception {
        mockMvc.perform(get(Constants.QUERY_PRODUCTS_PATH + "/prices").param("name", SHARED_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].price").value(19.99))
                .andExpect(jsonPath("$[1].price").value(39.99))
                .andExpect(jsonPath("$[2].price").value(59.99));
    }

    @Test
    void shouldReturnTheCheapestProductOfAName() throws Exception {
        mockMvc.perform(get(Constants.QUERY_PRODUCTS_PATH + "/lowest-price").param("name", SHARED_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(19.99))
                .andExpect(jsonPath("$.providerId").value(2));
    }
}
