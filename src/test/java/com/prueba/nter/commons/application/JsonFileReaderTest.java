package com.prueba.nter.commons.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prueba.nter.error.exception.InvalidFileException;
import com.prueba.nter.modules.users.infrastructure.dto.input.UserInputDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link JsonFileReader}.
 */
class JsonFileReaderTest {

    private final JsonFileReader jsonFileReader = new JsonFileReader(objectMapper(), validator());

    private static ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private static Validator validator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Test
    void shouldReadValidJsonList() {
        MockMultipartFile file = new MockMultipartFile("file", "Users.json", "application/json", """
                [{"username":"Laura","email":"laura@gmail.es","createdAt":"2019-05-10"}]
                """.getBytes());

        List<UserInputDto> users = jsonFileReader.read(file, UserInputDto.class);

        assertThat(users).singleElement()
                .extracting(UserInputDto::email)
                .isEqualTo("laura@gmail.es");
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "Users.json", "application/json", new byte[0]);

        assertThatThrownBy(() -> jsonFileReader.read(file, UserInputDto.class))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("missing or empty");
    }

    @Test
    void shouldRejectMalformedJson() {
        MockMultipartFile file = new MockMultipartFile("file", "Users.json", "application/json",
                "{ not a list".getBytes());

        assertThatThrownBy(() -> jsonFileReader.read(file, UserInputDto.class))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("valid JSON list");
    }

    @Test
    void shouldRejectEntriesBreakingValidationConstraints() {
        MockMultipartFile file = new MockMultipartFile("file", "Users.json", "application/json", """
                [{"username":"","email":"not-an-email","createdAt":"2019-05-10"}]
                """.getBytes());

        assertThatThrownBy(() -> jsonFileReader.read(file, UserInputDto.class))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("invalid entries");
    }
}
