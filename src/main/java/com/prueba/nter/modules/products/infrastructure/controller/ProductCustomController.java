package com.prueba.nter.modules.products.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.modules.products.application.service.port.ProductCustomService;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductFilterDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing Products with Custom queries.
 */
@RestController
@RequestMapping(Constants.CUSTOM_PRODUCTS_PATH)
@RequiredArgsConstructor
@Validated
public class ProductCustomController {

    private final ProductCustomService productCustomService;

    /**
     * Searches products by the optional name and category query parameters.
     *
     * @param name     the product name, optional
     * @param category the product category, optional
     * @return the matching products
     */
    @GetMapping
    public ResponseEntity<List<ProductOutputDto>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(productCustomService.searchByNameAndCategory(name, category));
    }

    /**
     * Searches products cheaper than the given price.
     *
     * @param price the exclusive upper price bound
     * @return the matching products
     */
    @GetMapping("/price-lower-than")
    public ResponseEntity<List<ProductOutputDto>> searchByPriceLowerThan(
            @RequestParam @NotNull @PositiveOrZero BigDecimal price) {
        return ResponseEntity.ok(productCustomService.searchByPriceLowerThan(price));
    }

    /**
     * Searches products expiring within the given date range, sorted by descending identifier.
     *
     * @param startDate the lower bound of the expiration date, inclusive
     * @param endDate   the upper bound of the expiration date, inclusive
     * @return the matching products
     */
    @GetMapping("/expiration-range")
    public ResponseEntity<List<ProductOutputDto>> searchByExpirationDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(productCustomService.searchByExpirationDateRange(startDate, endDate));
    }

    /**
     * Searches the products owned by the user with the given email, optionally
     * narrowing the result by category and brand.
     *
     * @param email    the owner email
     * @param category the product category, optional
     * @param brand    the product brand, optional
     * @return the matching products
     */
    @GetMapping("/by-user")
    public ResponseEntity<List<ProductOutputDto>> searchByUserEmail(
            @RequestParam @Email String email,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand) {
        return ResponseEntity.ok(productCustomService.searchByUserEmail(email, category, brand));
    }

    /**
     * Searches the products owned by the N oldest users of the system.
     *
     * @param users the number of users to consider
     * @return the matching products
     */
    @GetMapping("/oldest-users")
    public ResponseEntity<List<ProductOutputDto>> searchByOldestUsers(
            @RequestParam @Positive int users) {
        return ResponseEntity.ok(productCustomService.searchByOldestUsers(users));
    }

    /**
     * Performs an advanced search combining optional filters, pagination and sorting.
     *
     * @param name       partial product name, optional
     * @param category   product category, optional
     * @param brand      product brand, optional
     * @param minPrice   minimum price, optional
     * @param maxPrice   maximum price, optional
     * @param startDate  minimum expiration date, optional
     * @param endDate    maximum expiration date, optional
     * @param providerId provider identifier, optional
     * @param page       zero based page index
     * @param size       page size
     * @param sortBy     property used to sort the result
     * @param direction  sort direction, either {@code ASC} or {@code DESC}
     * @return a page of matching products
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductOutputDto>> searchAdvanced(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) @PositiveOrZero BigDecimal minPrice,
            @RequestParam(required = false) @PositiveOrZero BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long providerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = Constants.FIELD_ID) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        ProductFilterDto filter = ProductFilterDto.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .startDate(startDate)
                .endDate(endDate)
                .providerId(providerId)
                .build();
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return ResponseEntity.ok(productCustomService.searchAdvanced(filter, PageRequest.of(page, size, sort)));
    }
}
