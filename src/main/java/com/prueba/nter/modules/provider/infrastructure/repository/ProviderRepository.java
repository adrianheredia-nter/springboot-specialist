package com.prueba.nter.modules.provider.infrastructure.repository;

import com.prueba.nter.modules.provider.domain.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link ProviderEntity}.
 */
@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {
}
