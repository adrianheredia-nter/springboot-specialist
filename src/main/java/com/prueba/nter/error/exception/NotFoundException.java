package com.prueba.nter.error.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Thrown when a requested resource does not exist.
 */
public class NotFoundException extends RuntimeException {

    /** Message template used when an entity is not found by its identifier. */
    public static final String MESSAGE = "{0} with id {1} not found!";
    /** Message template used when an entity is not found by one of its fields. */
    public static final String MESSAGE_BY_FIELD = "{0} with {1} {2} not found!";

    /**
     * Creates the exception with the given detail message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Builds a supplier of this exception for an entity missing by identifier.
     *
     * @param entity the entity name
     * @param id     the identifier used in the lookup
     * @return a supplier creating the exception lazily
     */
    public static Supplier<NotFoundException> supply(String entity, Object id) {
        return () -> new NotFoundException(MessageFormat.format(MESSAGE, entity, id));
    }

    /**
     * Builds a supplier of this exception for an entity missing by a field value.
     *
     * @param entity the entity name
     * @param field  the field used in the lookup
     * @param value  the value used in the lookup
     * @return a supplier creating the exception lazily
     */
    public static Supplier<NotFoundException> supplyByField(String entity, String field, Object value) {
        return () -> new NotFoundException(MessageFormat.format(MESSAGE_BY_FIELD, entity, field, value));
    }
}
