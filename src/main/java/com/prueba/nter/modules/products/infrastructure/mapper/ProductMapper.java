package com.prueba.nter.modules.products.infrastructure.mapper;

import com.prueba.nter.commons.infrastructure.BaseMapper;
import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductInputDto;
import com.prueba.nter.modules.products.infrastructure.dto.ouput.ProductOutputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Maps products between entities and data transfer objects.
 */
@Mapper(config = BaseMapper.class)
public interface ProductMapper {

    /**
     * Maps an entity to its outgoing representation.
     *
     * @param entity the product entity
     * @return the outgoing representation
     */
    @Mapping(target = "providerId", source = "provider.id")
    @Mapping(target = "providerName", source = "provider.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    ProductOutputDto toOutput(ProductEntity entity);

    /**
     * Maps a list of entities to their outgoing representation.
     *
     * @param entities the product entities
     * @return the outgoing representations
     */
    List<ProductOutputDto> toOutputList(List<ProductEntity> entities);

    /**
     * Maps an incoming representation to a new entity, leaving the relations unset.
     *
     * @param dto the incoming representation
     * @return the product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "user", ignore = true)
    ProductEntity toEntity(ProductInputDto dto);
}
