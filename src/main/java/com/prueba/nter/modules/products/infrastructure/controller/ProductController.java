package com.prueba.nter.modules.products.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.modules.products.application.service.port.ProductService;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductCountDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductTotalPriceDto;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for managing Products.
 */
@RestController
@RequestMapping(Constants.WEB_PRODUCTS_PATH)
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * Processes a {@code Products.json} file and stores its products.
     *
     * @param file the uploaded JSON file
     * @return the stored products
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ProductOutputDto>> upload(
            @RequestParam(Constants.FILE_PARAM) MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.importFromFile(file));
    }

    /**
     * Retrieves every product stored in the database.
     *
     * @return the stored products
     */
    @GetMapping
    public ResponseEntity<List<ProductOutputDto>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    /**
     * Calculates the total price of the products contained in the uploaded file.
     *
     * @param file the uploaded JSON file
     * @return the number of products read and the sum of their prices
     */
    @PostMapping(path = "/total-price", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductTotalPriceDto> getTotalPrice(
            @RequestParam(Constants.FILE_PARAM) MultipartFile file) {
        return ResponseEntity.ok(productService.getTotalPriceFromFile(file));
    }

    /**
     * Counts the products of the given category contained in the uploaded file.
     *
     * @param file     the uploaded JSON file
     * @param category the category to filter by
     * @return the number of products of that category
     */
    @PostMapping(path = "/count-by-category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductCountDto> countByCategory(
            @RequestParam(Constants.FILE_PARAM) MultipartFile file,
            @RequestParam @NotBlank String category) {
        return ResponseEntity.ok(
                new ProductCountDto(category, productService.countByCategoryFromFile(file, category)));
    }
}
