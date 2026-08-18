package com.prueba.nter.modules.users.infrastructure.controller;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.support.JsonFiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link UserController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldUploadUsersJsonAndPersistThem() throws Exception {
        mockMvc.perform(multipart(Constants.WEB_USERS_PATH).file(JsonFiles.users()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].username").value("Laura Fernández"))
                .andExpect(jsonPath("$[0].email").value("laura.fernandez@gmail.es"))
                .andExpect(jsonPath("$[0].createdAt").value("2019-05-10"));

        mockMvc.perform(get(Constants.WEB_USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)));
    }

    @Test
    void shouldReturnTheSeedUsersBeforeAnyUpload() throws Exception {
        mockMvc.perform(get(Constants.WEB_USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].email").value("juan.perez@gmail.es"));
    }

    @Test
    void shouldReturnConflictWhenAnEmailIsAlreadyRegistered() throws Exception {
        MockMultipartFile duplicated = new MockMultipartFile(
                Constants.FILE_PARAM, "Users.json", "application/json", """
                [{"username":"Juan Pérez","email":"juan.perez@gmail.es","createdAt":"2020-01-15"}]
                """.getBytes());

        mockMvc.perform(multipart(Constants.WEB_USERS_PATH).file(duplicated))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectAFileWithInvalidUsers() throws Exception {
        MockMultipartFile invalid = new MockMultipartFile(
                Constants.FILE_PARAM, "Users.json", "application/json", """
                [{"username":"Laura","email":"not-an-email","createdAt":"2019-05-10"}]
                """.getBytes());

        mockMvc.perform(multipart(Constants.WEB_USERS_PATH).file(invalid))
                .andExpect(status().isBadRequest());
    }
}
