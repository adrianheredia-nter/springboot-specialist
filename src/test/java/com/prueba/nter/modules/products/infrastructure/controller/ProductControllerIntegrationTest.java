package com.prueba.nter.modules.products.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.support.JsonFiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link ProductController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldUploadProductsJsonAndPersistThem() throws Exception {
        mockMvc.perform(multipart(Constants.WEB_PRODUCTS_PATH).file(JsonFiles.products()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].name").value("Disco Duro SSD"))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].providerName").value("TechProvider S.A."))
                .andExpect(jsonPath("$[0].userEmail").value("juan.perez@gmail.es"));

        mockMvc.perform(get(Constants.WEB_PRODUCTS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(30)));
    }

    @Test
    void shouldReturnConflictWhenTheSameFileIsUploadedTwice() throws Exception {
        mockMvc.perform(multipart(Constants.WEB_PRODUCTS_PATH).file(JsonFiles.products()))
                .andExpect(status().isCreated());

        mockMvc.perform(multipart(Constants.WEB_PRODUCTS_PATH).file(JsonFiles.products()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void shouldReturnTheSeedProductsBeforeAnyUpload() throws Exception {
        mockMvc.perform(get(Constants.WEB_PRODUCTS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(20)));
    }

    @Test
    void shouldCalculateTheTotalPriceOfTheUploadedFile() throws Exception {
        mockMvc.perform(multipart(Constants.WEB_PRODUCTS_PATH + "/total-price").file(JsonFiles.products()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").value(10))
                .andExpect(jsonPath("$.totalPrice").value(759.90));
    }

    @Test
    void shouldCountTheUploadedProductsByCategory() throws Exception {
        mockMvc.perform(multipart(Constants.WEB_PRODUCTS_PATH + "/count-by-category")
                        .file(JsonFiles.products())
                        .param("category", "Electrónica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Electrónica"))
                .andExpect(jsonPath("$.total").value(4));
    }

    @Test
    void shouldRejectAnEmptyFile() throws Exception {
        MockMultipartFile empty = new MockMultipartFile(
                Constants.FILE_PARAM, "Products.json", "application/json", new byte[0]);

        mockMvc.perform(multipart(Constants.WEB_PRODUCTS_PATH).file(empty))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constants.ERROR_EMPTY_FILE));
    }

    @Test
    void shouldRejectAFileWithInvalidProducts() throws Exception {
        MockMultipartFile invalid = new MockMultipartFile(
                Constants.FILE_PARAM, "Products.json", "application/json",
                "[{\"name\":\"\",\"price\":-1,\"category\":\"\"}]".getBytes());

        mockMvc.perform(multipart(Constants.WEB_PRODUCTS_PATH).file(invalid))
                .andExpect(status().isBadRequest());
    }
}
