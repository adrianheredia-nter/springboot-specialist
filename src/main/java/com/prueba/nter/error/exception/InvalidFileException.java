package com.prueba.nter.error.exception;

/**
 * Thrown when an uploaded file is missing, empty or does not contain valid data.
 */
public class InvalidFileException extends RuntimeException {

    /**
     * Creates the exception with the given detail message.
     *
     * @param message the detail message
     */
    public InvalidFileException(String message) {
        super(message);
    }
}
