package com.prueba.nter.error;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.error.exception.AlreadyExistsException;
import com.prueba.nter.error.exception.InvalidFileException;
import com.prueba.nter.error.exception.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Translates application exceptions into HTTP responses carrying a {@link CustomError}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles missing resources.
     *
     * @param exception the caught exception
     * @return a {@code 404} response
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomError> handleNotFound(NotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Handles unique constraint violations detected by the application.
     *
     * @param exception the caught exception
     * @return a {@code 409} response
     */
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<CustomError> handleAlreadyExists(AlreadyExistsException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Handles unique constraint violations detected by the database.
     *
     * @param exception the caught exception
     * @return a {@code 409} response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomError> handleDataIntegrity(DataIntegrityViolationException exception) {
        return build(HttpStatus.CONFLICT, Constants.ERROR_DATA_INTEGRITY);
    }

    /**
     * Handles invalid uploaded files.
     *
     * @param exception the caught exception
     * @return a {@code 400} response
     */
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<CustomError> handleInvalidFile(InvalidFileException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * Handles invalid request bodies.
     *
     * @param exception the caught exception
     * @return a {@code 400} response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(this::describe)
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles invalid request parameters.
     *
     * @param exception the caught exception
     * @return a {@code 400} response
     */
    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<CustomError> handleBadRequest(RuntimeException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private String describe(FieldError fieldError) {
        return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }

    private ResponseEntity<CustomError> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new CustomError(message, LocalDateTime.now()));
    }
}
