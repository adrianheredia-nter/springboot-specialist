package com.prueba.nter.modules.products.application.service;

import com.prueba.nter.error.exception.NotFoundException;
import com.prueba.nter.modules.products.infrastructure.mapper.ProductMapper;
import com.prueba.nter.modules.products.infrastructure.repository.ProductRepository;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the guard clauses of {@link ProductCustomServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ProductCustomServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductCustomServiceImpl productCustomService;

    @Test
    void shouldRejectInvertedExpirationDateRange() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 1);

        assertThatThrownBy(() -> productCustomService.searchByExpirationDateRange(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("startDate");
        verify(productRepository, never()).searchByExpirationDateRange(any(), any());
    }

    @Test
    void shouldRejectNonPositiveNumberOfUsers() {
        assertThatThrownBy(() -> productCustomService.searchByOldestUsers(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("users");
        verify(productRepository, never()).searchByOldestUsers(anyInt());
    }

    @Test
    void shouldRejectUnknownUserEmail() {
        when(userRepository.findByEmail("ghost@gmail.es")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productCustomService.searchByUserEmail("ghost@gmail.es", null, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ghost@gmail.es");
        verify(productRepository, never()).searchByUserEmail(anyString(), any(), any());
    }
}
