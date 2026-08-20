package com.prueba.nter.modules.users.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.modules.users.application.service.port.UserService;
import com.prueba.nter.modules.users.infrastructure.dto.ouput.UserOutputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for managing Users.
 */
@RestController
@RequestMapping(Constants.WEB_USERS_PATH)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Processes a {@code Users.json} file and stores its users.
     *
     * @param file the uploaded JSON file
     * @return the stored users
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UserOutputDto>> upload(
            @RequestParam(Constants.FILE_PARAM) MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.importFromFile(file));
    }

    /**
     * Retrieves every user stored in the database.
     *
     * @return the stored users
     */
    @GetMapping
    public ResponseEntity<List<UserOutputDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }
}
