package com.prueba.nter.commons.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.nter.commons.Constants;
import com.prueba.nter.error.exception.InvalidFileException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reads and validates a JSON list contained in an uploaded multipart file.
 */
@Component
@RequiredArgsConstructor
public class JsonFileReader {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * Parses the given multipart file as a JSON array of the requested type and
     * validates every element with the Bean Validation constraints it declares.
     *
     * @param file the uploaded JSON file
     * @param type the type of the elements contained in the JSON array
     * @param <T>  the type of the elements contained in the JSON array
     * @return the parsed and validated elements
     * @throws InvalidFileException if the file is empty, unparseable or invalid
     */
    public <T> List<T> read(MultipartFile file, Class<T> type) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(Constants.ERROR_EMPTY_FILE);
        }
        List<T> items = parse(file, type);
        validate(items);
        return items;
    }

    private <T> List<T> parse(MultipartFile file, Class<T> type) {
        try (InputStream inputStream = file.getInputStream()) {
            return objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new InvalidFileException(
                    MessageFormat.format(Constants.ERROR_INVALID_JSON, e.getMessage()));
        }
    }

    private <T> void validate(List<T> items) {
        String violations = items.stream()
                .map(validator::validate)
                .flatMap(Set::stream)
                .map(this::describe)
                .collect(Collectors.joining(", "));
        if (!violations.isEmpty()) {
            throw new InvalidFileException(
                    MessageFormat.format(Constants.ERROR_INVALID_CONTENT, violations));
        }
    }

    private String describe(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + " " + violation.getMessage();
    }
}
