package com.prueba.nter.modules.products.application.service;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.error.exception.NotFoundException;
import com.prueba.nter.modules.products.application.service.port.ProductQueryService;
import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import com.prueba.nter.modules.products.infrastructure.mapper.ProductMapper;
import com.prueba.nter.modules.products.infrastructure.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link ProductQueryService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductOutputDto> findByName(String name) {
        return productMapper.toOutputList(requireNotEmpty(
                productRepository.findByProductName(name), Constants.FIELD_NAME, name));
    }

    @Override
    public long countByCategory(String category) {
        return productRepository.countByProductCategory(category);
    }

    @Override
    public List<ProductOutputDto> findByNameAndCategory(String name, String category) {
        return productMapper.toOutputList(requireNotEmpty(
                productRepository.findByNameAndCategory(name, category), Constants.FIELD_NAME, name));
    }

    @Override
    public List<ProductOutputDto> findPricesByName(String name) {
        return productMapper.toOutputList(requireNotEmpty(
                productRepository.findPricesByNameOrderByPriceAsc(name), Constants.FIELD_NAME, name));
    }

    @Override
    public ProductOutputDto findCheapestByName(String name) {
        return productMapper.toOutput(requireNotEmpty(
                productRepository.findCheapestByName(name), Constants.FIELD_NAME, name).getFirst());
    }

    private List<ProductEntity> requireNotEmpty(List<ProductEntity> products, String field, Object value) {
        if (products.isEmpty()) {
            throw NotFoundException.supplyByField(Constants.PRODUCT, field, value).get();
        }
        return products;
    }
}
