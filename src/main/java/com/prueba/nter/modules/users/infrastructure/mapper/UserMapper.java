package com.prueba.nter.modules.users.infrastructure.mapper;

import com.prueba.nter.commons.infrastructure.BaseMapper;
import com.prueba.nter.modules.users.domain.UserEntity;
import com.prueba.nter.modules.users.infrastructure.dto.input.UserInputDto;
import com.prueba.nter.modules.users.infrastructure.dto.ouput.UserOutputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Maps users between entities and data transfer objects.
 */
@Mapper(config = BaseMapper.class)
public interface UserMapper {

    /**
     * Maps an entity to its outgoing representation.
     *
     * @param entity the user entity
     * @return the outgoing representation
     */
    UserOutputDto toOutput(UserEntity entity);

    /**
     * Maps a list of entities to their outgoing representation.
     *
     * @param entities the user entities
     * @return the outgoing representations
     */
    List<UserOutputDto> toOutputList(List<UserEntity> entities);

    /**
     * Maps an incoming representation to a new entity.
     *
     * @param dto the incoming representation
     * @return the user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    UserEntity toEntity(UserInputDto dto);
}
