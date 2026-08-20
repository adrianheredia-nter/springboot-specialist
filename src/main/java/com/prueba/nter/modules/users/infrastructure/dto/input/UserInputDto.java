package com.prueba.nter.modules.users.infrastructure.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Incoming representation of a user, matching the structure of {@code Users.json}.
 *
 * @param username  display name of the user
 * @param email     unique email of the user
 * @param createdAt date when the user was registered
 */
public record UserInputDto(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotNull LocalDate createdAt) {

    /**
     * Returns this input with its email in the canonical form used for storage.
     *
     * @return a copy with a lower-case email
     */
    public UserInputDto normalized() {
        return new UserInputDto(username, email.toLowerCase(Locale.ROOT), createdAt);
    }
}
