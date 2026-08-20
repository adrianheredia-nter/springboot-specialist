package com.prueba.nter.modules.products.application.service.port;

import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductTotalPriceDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing products.
 */
public interface ProductService {

    /**
     * Retrieves all products.
     *
     * @return a list of all products
     */
    List<ProductOutputDto> getAll();

    /**
     * Processes a {@code Products.json} file and stores its products.
     *
     * @param file the uploaded JSON file
     * @return the stored products
     */
    List<ProductOutputDto> importFromFile(MultipartFile file);

    /**
     * Calculates the total price of the products contained in the uploaded file.
     *
     * @param file the uploaded JSON file
     * @return the number of products read and the sum of their prices
     */
    ProductTotalPriceDto getTotalPriceFromFile(MultipartFile file);

    /**
     * Counts the products of the given category contained in the uploaded file.
     *
     * @param file     the uploaded JSON file
     * @param category the category to filter by
     * @return the number of products of that category in the file
     */
    long countByCategoryFromFile(MultipartFile file, String category);
}
