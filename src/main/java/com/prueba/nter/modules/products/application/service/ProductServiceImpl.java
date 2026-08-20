package com.prueba.nter.modules.products.application.service;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.commons.application.JsonFileReader;
import com.prueba.nter.error.exception.AlreadyExistsException;
import com.prueba.nter.modules.products.application.service.port.ProductService;
import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductInputDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductTotalPriceDto;
import com.prueba.nter.modules.products.infrastructure.mapper.ProductMapper;
import com.prueba.nter.modules.products.infrastructure.repository.ProductRepository;
import com.prueba.nter.modules.provider.domain.ProviderEntity;
import com.prueba.nter.modules.provider.infrastructure.repository.ProviderRepository;
import com.prueba.nter.error.exception.NotFoundException;
import com.prueba.nter.modules.users.domain.UserEntity;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link ProductService}.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final JsonFileReader jsonFileReader;

    @Override
    @Transactional(readOnly = true)
    public List<ProductOutputDto> getAll() {
        return productMapper.toOutputList(productRepository.findAll());
    }

    @Override
    @Transactional
    public List<ProductOutputDto> importFromFile(MultipartFile file) {
        List<ProductInputDto> inputs = jsonFileReader.read(file, ProductInputDto.class);
        Set<String> processed = new HashSet<>();
        List<ProductEntity> products = new ArrayList<>();

        for (ProductInputDto input : inputs) {
            checkUniqueness(input, processed);
            ProductEntity product = productMapper.toEntity(input);
            product.setProvider(findProvider(input.providerId()));
            product.setUser(findUser(input.userId()));
            products.add(product);
        }
        return productMapper.toOutputList(productRepository.saveAll(products));
    }

    @Override
    public ProductTotalPriceDto getTotalPriceFromFile(MultipartFile file) {
        List<ProductInputDto> products = jsonFileReader.read(file, ProductInputDto.class);
        BigDecimal totalPrice = products.stream()
                .map(ProductInputDto::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ProductTotalPriceDto(products.size(), totalPrice);
    }

    @Override
    public long countByCategoryFromFile(MultipartFile file, String category) {
        return jsonFileReader.read(file, ProductInputDto.class).stream()
                .filter(product -> product.category().equalsIgnoreCase(category))
                .count();
    }

    private void checkUniqueness(ProductInputDto input, Set<String> processed) {
        String key = input.name() + "#" + input.providerId();
        if (!processed.add(key) || productRepository.existsByNameAndProviderId(input.name(), input.providerId())) {
            throw new AlreadyExistsException(MessageFormat.format(
                    Constants.ERROR_PRODUCT_EXISTS, input.name(), String.valueOf(input.providerId())));
        }
    }

    private ProviderEntity findProvider(Long providerId) {
        return providerRepository.findById(providerId)
                .orElseThrow(NotFoundException.supply(Constants.PROVIDER, providerId));
    }

    private UserEntity findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(NotFoundException.supply(Constants.USER, userId));
    }
}
