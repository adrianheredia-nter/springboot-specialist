package com.prueba.nter.modules.users.application.service.port;

import com.prueba.nter.modules.users.infrastructure.dto.ouput.UserOutputDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService {

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    List<UserOutputDto> getAll();

    /**
     * Processes a {@code Users.json} file and stores its users.
     *
     * @param file the uploaded JSON file
     * @return the stored users
     */
    List<UserOutputDto> importFromFile(MultipartFile file);
}
