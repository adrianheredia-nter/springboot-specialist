package com.prueba.nter.support;

import com.prueba.nter.commons.Constants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Test helper building multipart files from the JSON files bundled with the application.
 */
public final class JsonFiles {

    private static final String PRODUCTS = "Products.json";
    private static final String USERS = "Users.json";

    private JsonFiles() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Builds a multipart file holding the bundled {@code Products.json}.
     *
     * @return the multipart file
     * @throws IOException if the resource cannot be read
     */
    public static MockMultipartFile products() throws IOException {
        return read(PRODUCTS);
    }

    /**
     * Builds a multipart file holding the bundled {@code Users.json}.
     *
     * @return the multipart file
     * @throws IOException if the resource cannot be read
     */
    public static MockMultipartFile users() throws IOException {
        return read(USERS);
    }

    private static MockMultipartFile read(String name) throws IOException {
        try (InputStream inputStream = new ClassPathResource(name).getInputStream()) {
            return new MockMultipartFile(
                    Constants.FILE_PARAM, name, MediaType.APPLICATION_JSON_VALUE, inputStream.readAllBytes());
        }
    }
}
