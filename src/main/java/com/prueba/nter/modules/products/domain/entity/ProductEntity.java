package com.prueba.nter.modules.products.domain.entity;

import com.prueba.nter.modules.provider.domain.ProviderEntity;
import com.prueba.nter.modules.users.domain.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a product entity.
 *
 * <p>A product belongs to a single {@link ProviderEntity} and to a single
 * {@link UserEntity}. The pair {@code (name, provider)} is unique, so the same
 * provider cannot supply two products sharing the same name.</p>
 */
@Entity
@Table(
        name = "products",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_products_name_provider",
                columnNames = {"name", "provider_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    /** Unique identifier of the product. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Commercial name of the product. */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /** Free text description of the product. */
    @Column(length = 500)
    private String description;

    /** Unit price of the product. */
    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** Units available in stock. */
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantity;

    /** Category the product belongs to. */
    @NotBlank
    @Column(nullable = false)
    private String category;

    /** Brand of the product. */
    private String brand;

    /** Date when the product expires. */
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    /** Provider supplying the product. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    /** User owning the product. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
