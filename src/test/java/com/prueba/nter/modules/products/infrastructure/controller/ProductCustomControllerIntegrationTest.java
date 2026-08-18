package com.prueba.nter.modules.products.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link ProductCustomController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductCustomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSearchByOptionalNameAndCategory() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(20)));

        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH)
                        .param("name", "portátil")
                        .param("category", "Electrónica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Portátil"));
    }

    @Test
    void shouldSearchProductsCheaperThanTheGivenPrice() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/price-lower-than").param("price", "50.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].price", everyItem(lessThan(50.00))));
    }

    @Test
    void shouldSearchProductsWithinAnExpirationDateRangeSortedByDescendingId() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/expiration-range")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[3].id").value(2));
    }

    @Test
    void shouldRejectAnInvertedExpirationDateRange() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/expiration-range")
                        .param("startDate", "2025-12-31")
                        .param("endDate", "2024-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constants.ERROR_DATE_RANGE));
    }

    @Test
    void shouldSearchTheProductsOfAUserFilteringByCategoryAndBrand() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/by-user")
                        .param("email", "maria.gomez@gmail.es"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));

        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/by-user")
                        .param("email", "maria.gomez@gmail.es")
                        .param("category", "Electrónica")
                        .param("brand", "MarcaAudio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Auriculares Inalámbricos"));
    }

    @Test
    void shouldReturnNotFoundForAnUnknownUserEmail() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/by-user")
                        .param("email", "ghost@gmail.es"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchTheProductsOfTheOldestUsers() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/oldest-users").param("users", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void shouldRejectANonPositiveNumberOfUsers() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/oldest-users").param("users", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSearchAdvancedWithFiltersPaginationAndSorting() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/search")
                        .param("category", "Electrónica")
                        .param("providerId", "1")
                        .param("minPrice", "100")
                        .param("maxPrice", "1000")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sortBy", "price")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].price").value(999.99))
                .andExpect(jsonPath("$.totalElements").value(9))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void shouldRejectAnAdvancedSearchWithAnInvalidPageSize() throws Exception {
        mockMvc.perform(get(Constants.CUSTOM_PRODUCTS_PATH + "/search").param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}
