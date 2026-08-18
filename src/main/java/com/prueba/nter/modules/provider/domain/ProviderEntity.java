package com.prueba.nter.modules.provider.domain;

import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a provider that supplies products.
 */
@Entity
@Table(name = "provider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderEntity {

    /** Unique identifier of the provider. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fiscal identification code of the provider. */
    @NotBlank
    @Column(nullable = false, unique = true)
    private String cif;

    /** Commercial name of the provider. */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /** Products supplied by this provider. */
    @Builder.Default
    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductEntity> products = new ArrayList<>();
}
