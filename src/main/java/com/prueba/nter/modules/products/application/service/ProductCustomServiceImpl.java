package com.prueba.nter.modules.products.application.service;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.error.exception.NotFoundException;
import com.prueba.nter.modules.products.application.service.port.ProductCustomService;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductFilterDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import com.prueba.nter.modules.products.infrastructure.mapper.ProductMapper;
import com.prueba.nter.modules.products.infrastructure.repository.ProductRepository;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * Default implementation of {@link ProductCustomService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCustomServiceImpl implements ProductCustomService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductOutputDto> searchByNameAndCategory(String name, String category) {
        return productMapper.toOutputList(productRepository.searchByNameAndCategory(name, category));
    }

    @Override
    public List<ProductOutputDto> searchByPriceLowerThan(BigDecimal price) {
        return productMapper.toOutputList(productRepository.searchByPriceLowerThan(price));
    }

    @Override
    public List<ProductOutputDto> searchByExpirationDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(Constants.ERROR_DATE_RANGE);
        }
        return productMapper.toOutputList(productRepository.searchByExpirationDateRange(startDate, endDate));
    }

    @Override
    public List<ProductOutputDto> searchByUserEmail(String email, String category, String brand) {
        userRepository.findByEmail(email)
                .orElseThrow(NotFoundException.supplyByField(Constants.USER, Constants.FIELD_EMAIL, email));
        return productMapper.toOutputList(productRepository.searchByUserEmail(email, category, brand));
    }

    @Override
    public List<ProductOutputDto> searchByOldestUsers(int users) {
        if (users <= 0) {
            throw new IllegalArgumentException(
                    MessageFormat.format(Constants.ERROR_POSITIVE_NUMBER, "users"));
        }
        return productMapper.toOutputList(productRepository.searchByOldestUsers(users));
    }

    @Override
    public Page<ProductOutputDto> searchAdvanced(ProductFilterDto filter, Pageable pageable) {
        return productRepository.searchAdvanced(filter, pageable).map(productMapper::toOutput);
    }
}
