package com.prueba.nter.commons;

/**
 * Application wide constants.
 *
 * <p>Centralises repeated literals such as API paths, entity names, persistent
 * attribute names and error messages.</p>
 */
public final class Constants {

    /** API root path. */
    public static final String API_V1 = "/api/v1";
    /** Base path of the Spring Web product controller. */
    public static final String WEB_PRODUCTS_PATH = API_V1 + "/web/products";
    /** Base path of the Spring Web user controller. */
    public static final String WEB_USERS_PATH = API_V1 + "/web/users";
    /** Base path of the {@code @Query} based product controller. */
    public static final String QUERY_PRODUCTS_PATH = API_V1 + "/query/products";
    /** Base path of the Criteria API based product controller. */
    public static final String CUSTOM_PRODUCTS_PATH = API_V1 + "/custom/products";

    /** Multipart request parameter used to upload the JSON files. */
    public static final String FILE_PARAM = "file";

    /** Product entity name, used in error messages. */
    public static final String PRODUCT = "Product";
    /** User entity name, used in error messages. */
    public static final String USER = "User";
    /** Provider entity name, used in error messages. */
    public static final String PROVIDER = "Provider";

    /** {@code ProductEntity#id} attribute name. */
    public static final String FIELD_ID = "id";
    /** {@code ProductEntity#name} attribute name. */
    public static final String FIELD_NAME = "name";
    /** {@code ProductEntity#category} attribute name. */
    public static final String FIELD_CATEGORY = "category";
    /** {@code ProductEntity#brand} attribute name. */
    public static final String FIELD_BRAND = "brand";
    /** {@code ProductEntity#price} attribute name. */
    public static final String FIELD_PRICE = "price";
    /** {@code ProductEntity#expirationDate} attribute name. */
    public static final String FIELD_EXPIRATION_DATE = "expirationDate";
    /** {@code ProductEntity#provider} attribute name. */
    public static final String FIELD_PROVIDER = "provider";
    /** {@code ProductEntity#user} attribute name. */
    public static final String FIELD_USER = "user";
    /** {@code UserEntity#email} attribute name. */
    public static final String FIELD_EMAIL = "email";
    /** {@code UserEntity#createdAt} attribute name. */
    public static final String FIELD_CREATED_AT = "createdAt";

    /** Error message used when the uploaded file is missing or empty. */
    public static final String ERROR_EMPTY_FILE = "The uploaded file is missing or empty!";
    /** Error message used when the uploaded file cannot be parsed. */
    public static final String ERROR_INVALID_JSON = "The uploaded file does not contain a valid JSON list: {0}";
    /** Error message used when the uploaded file contains invalid entries. */
    public static final String ERROR_INVALID_CONTENT = "The uploaded file contains invalid entries: {0}";
    /** Error message used when a product already exists for a provider. */
    public static final String ERROR_PRODUCT_EXISTS = "Product {0} already exists for provider with id {1}!";
    /** Error message used when a user email is already registered. */
    public static final String ERROR_USER_EXISTS = "User with email {0} already exists!";
    /** Error message used when the database rejects duplicated data. */
    public static final String ERROR_DATA_INTEGRITY = "The data sent violates a uniqueness constraint!";
    /** Error message used when a positive number is expected. */
    public static final String ERROR_POSITIVE_NUMBER = "The parameter {0} must be greater than 0!";
    /** Error message used when a date range is inconsistent. */
    public static final String ERROR_DATE_RANGE = "startDate must not be after endDate!";

    private Constants() {
        throw new IllegalStateException("Constants class");
    }
}
