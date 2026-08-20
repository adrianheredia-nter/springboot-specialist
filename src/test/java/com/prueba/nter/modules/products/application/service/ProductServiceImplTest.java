package com.prueba.nter.modules.products.application.service;

import com.prueba.nter.commons.application.JsonFileReader;
import com.prueba.nter.error.exception.AlreadyExistsException;
import com.prueba.nter.error.exception.NotFoundException;
import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductInputDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductTotalPriceDto;
import com.prueba.nter.modules.products.infrastructure.mapper.ProductMapper;
import com.prueba.nter.modules.products.infrastructure.repository.ProductRepository;
import com.prueba.nter.modules.provider.domain.ProviderEntity;
import com.prueba.nter.modules.provider.infrastructure.repository.ProviderRepository;
import com.prueba.nter.modules.users.domain.UserEntity;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProductServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceImplTest {

    private static final MultipartFile FILE =
            new MockMultipartFile("file", "Products.json", "application/json", "[]".getBytes());

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProviderRepository providerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private JsonFileReader jsonFileReader;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductInputDto input(String name, BigDecimal price, String category) {
        return new ProductInputDto(name, "description", price, 10, category, "brand",
                LocalDate.of(2026, 6, 15), 1L, 1L);
    }

    @Test
    void shouldImportProductsResolvingRelations() {
        ProductInputDto input = input("Disco Duro SSD", new BigDecimal("89.99"), "Electronics");
        ProviderEntity provider = ProviderEntity.builder().id(1L).cif("A1").name("TechProvider").build();
        UserEntity user = UserEntity.builder().id(1L).username("Juan").email("juan@gmail.es")
                .createdAt(LocalDate.of(2020, 1, 15)).build();

        when(jsonFileReader.read(FILE, ProductInputDto.class)).thenReturn(List.of(input));
        when(productRepository.existsByNameAndProviderId(anyString(), eq(1L))).thenReturn(false);
        when(productMapper.toEntity(input)).thenReturn(ProductEntity.builder().name(input.name()).build());
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        productService.importFromFile(FILE);

        ArgumentCaptor<List<ProductEntity>> captor = ArgumentCaptor.captor();
        verify(productRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).singleElement().satisfies(product -> {
            assertThat(product.getProvider()).isEqualTo(provider);
            assertThat(product.getUser()).isEqualTo(user);
        });
    }

    @Test
    void shouldRejectProductAlreadyStoredForTheSameProvider() {
        ProductInputDto input = input("Disco Duro SSD", new BigDecimal("89.99"), "Electronics");
        when(jsonFileReader.read(FILE, ProductInputDto.class)).thenReturn(List.of(input));
        when(productRepository.existsByNameAndProviderId("Disco Duro SSD", 1L)).thenReturn(true);

        assertThatThrownBy(() -> productService.importFromFile(FILE))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Disco Duro SSD");
        verify(productRepository, never()).saveAll(any());
    }

    @Test
    void shouldRejectDuplicatedProductWithinTheSameFile() {
        ProductInputDto input = input("Disco Duro SSD", new BigDecimal("89.99"), "Electronics");
        when(jsonFileReader.read(FILE, ProductInputDto.class)).thenReturn(List.of(input, input));
        when(productRepository.existsByNameAndProviderId(anyString(), eq(1L))).thenReturn(false);
        when(productMapper.toEntity(input)).thenReturn(ProductEntity.builder().name(input.name()).build());
        when(providerRepository.findById(1L)).thenReturn(Optional.of(ProviderEntity.builder().id(1L).build()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserEntity.builder().id(1L).build()));

        assertThatThrownBy(() -> productService.importFromFile(FILE))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void shouldRejectUnknownProvider() {
        ProductInputDto input = input("Disco Duro SSD", new BigDecimal("89.99"), "Electronics");
        when(jsonFileReader.read(FILE, ProductInputDto.class)).thenReturn(List.of(input));
        when(productMapper.toEntity(input)).thenReturn(ProductEntity.builder().name(input.name()).build());
        when(providerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.importFromFile(FILE))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Provider");
    }

    @Test
    void shouldRejectUnknownUser() {
        ProductInputDto input = input("Disco Duro SSD", new BigDecimal("89.99"), "Electronics");
        when(jsonFileReader.read(FILE, ProductInputDto.class)).thenReturn(List.of(input));
        when(productMapper.toEntity(input)).thenReturn(ProductEntity.builder().name(input.name()).build());
        when(providerRepository.findById(1L)).thenReturn(Optional.of(ProviderEntity.builder().id(1L).build()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.importFromFile(FILE))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void shouldCalculateTotalPriceOfTheFile() {
        when(jsonFileReader.read(FILE, ProductInputDto.class)).thenReturn(List.of(
                input("A", new BigDecimal("89.99"), "Electronics"),
                input("B", new BigDecimal("10.01"), "Furniture")));

        ProductTotalPriceDto total = productService.getTotalPriceFromFile(FILE);

        assertThat(total.products()).isEqualTo(2);
        assertThat(total.totalPrice()).isEqualByComparingTo("100.00");
    }

    @Test
    void shouldCountProductsOfTheFileByCategory() {
        when(jsonFileReader.read(FILE, ProductInputDto.class)).thenReturn(List.of(
                input("A", new BigDecimal("1.00"), "Electronics"),
                input("B", new BigDecimal("1.00"), "Furniture"),
                input("C", new BigDecimal("1.00"), "electronics")));

        assertThat(productService.countByCategoryFromFile(FILE, "Electronics")).isEqualTo(2);
        assertThat(productService.countByCategoryFromFile(FILE, "Shoes")).isZero();
    }
}
