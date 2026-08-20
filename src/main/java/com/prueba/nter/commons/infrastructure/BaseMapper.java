package com.prueba.nter.commons.infrastructure;

import org.mapstruct.*;

/**
 * This class acts as a config mapper for any of the entities including common properties required in mapping
 * <p>
 *     Defining common strategies in the mappers configurations
 * </p>
 * <ul>
 *     <li><code>componentModel</code> Required for the use of mapper in Spring boot</li>
 *     <li><code>nullValuePropertyMappingStrategy</code> Ignoring null values in mapping for source</li>
 *     <li><code>nullValueMappingStrategy</code> Ignoring null values in mapping for target</li>
 *     <li><code>unmappedTargetPolicy</code> Ignoring warnings for unmapped properties in mapping</li>
 *     <li><code>injectionStrategy</code> Best practice, default is field one</li>
 * </ul>
 */
@MapperConfig (
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface BaseMapper {
}
