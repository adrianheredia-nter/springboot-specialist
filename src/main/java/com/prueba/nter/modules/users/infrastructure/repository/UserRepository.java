package com.prueba.nter.modules.users.infrastructure.repository;

import com.prueba.nter.modules.users.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link UserEntity}.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds a user by its unique email.
     *
     * @param email the email to look for
     * @return the user, if any
     */
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    /**
     * Checks whether a user is already registered with the given email.
     *
     * @param email the email to look for
     * @return {@code true} if the email is already used
     */
    boolean existsByEmailIgnoreCase(String email);
}
