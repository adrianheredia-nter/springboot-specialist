package com.prueba.nter.modules.users.infrastructure.dto.ouput;

import java.time.LocalDate;

/**
 * Outgoing representation of a user.
 *
 * @param id        identifier of the user
 * @param username  display name of the user
 * @param email     unique email of the user
 * @param createdAt date when the user was registered
 */
public record UserOutputDto(
        Long id,
        String username,
        String email,
        LocalDate createdAt) {
}
