package com.prueba.nter.modules.products.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.modules.products.application.service.port.ProductQueryService;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductCountDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing Products with Queries.
 */
@RestController
@RequestMapping(Constants.QUERY_PRODUCTS_PATH)
@RequiredArgsConstructor
@Validated
public class ProductQueryController {

    private final ProductQueryService productQueryService;

    /**
     * Searches the products stored with the given name.
     *
     * @param name the product name
     * @return the matching products
     */
    @GetMapping("/by-name")
    public ResponseEntity<List<ProductOutputDto>> findByName(@RequestParam @NotBlank String name) {
        return ResponseEntity.ok(productQueryService.findByName(name));
    }

    /**
     * Counts the products stored in the given category.
     *
     * @param category the category to filter by
     * @return the number of products of that category
     */
    @GetMapping("/count-by-category")
    public ResponseEntity<ProductCountDto> countByCategory(@RequestParam @NotBlank String category) {
        return ResponseEntity.ok(new ProductCountDto(category, productQueryService.countByCategory(category)));
    }

    /**
     * Searches the products stored with the given name and category.
     *
     * @param name     the product name
     * @param category the product category
     * @return the matching products
     */
    @GetMapping("/by-name-and-category")
    public ResponseEntity<List<ProductOutputDto>> findByNameAndCategory(
            @RequestParam @NotBlank String name,
            @RequestParam @NotBlank String category) {
        return ResponseEntity.ok(productQueryService.findByNameAndCategory(name, category));
    }

    /**
     * Retrieves the prices of a product for every provider, sorted by ascending price.
     *
     * @param name the product name
     * @return the matching products sorted by ascending price
     */
    @GetMapping("/prices")
    public ResponseEntity<List<ProductOutputDto>> findPricesByName(@RequestParam @NotBlank String name) {
        return ResponseEntity.ok(productQueryService.findPricesByName(name));
    }

    /**
     * Retrieves the cheapest product for the given name.
     *
     * @param name the product name
     * @return the cheapest product
     */
    @GetMapping("/lowest-price")
    public ResponseEntity<ProductOutputDto> findCheapestByName(@RequestParam @NotBlank String name) {
        return ResponseEntity.ok(productQueryService.findCheapestByName(name));
    }
}
