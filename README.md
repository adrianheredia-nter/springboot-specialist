# Spring Boot Specialist Exam

REST API built with Spring Boot 3.4 and Java 21 that solves the *Examen práctico Specialist*: it reads the
provided `Products.json` and `Users.json` files, persists them in H2 and exposes the requested queries through
Spring Data JPA (`@Query`) and the Criteria API.

## Tech stack

| Concern         | Choice                                        |
|-----------------|-----------------------------------------------|
| Language        | Java 21                                       |
| Framework       | Spring Boot 3.4.4 (Web, Validation, Data JPA) |
| Persistence     | Hibernate + H2 (file based, console enabled)   |
| Mapping         | MapStruct 1.6.3                               |
| Boilerplate     | Lombok                                        |
| Build           | Maven Wrapper                                 |
| Tests           | JUnit 5, Mockito, MockMvc, `@DataJpaTest`     |

## Project layout

```
com.prueba.nter
├── commons                # Constants and the reusable JSON file reader
├── error                  # Custom exceptions and the @RestControllerAdvice
└── modules
    ├── products
    │   ├── application    # Service interfaces (ports) and implementations
    │   ├── domain         # ProductEntity
    │   └── infrastructure # Controllers, DTOs, MapStruct mappers, repositories
    ├── provider           # ProviderEntity and its repository
    └── users              # UserEntity, service, controller, DTOs and mapper
```

Each module follows the same layered structure: `infrastructure/controller` → `application/service` →
`infrastructure/repository` → `domain`. Controllers only speak DTOs, entities never leave the service layer.

## Domain model

* `ProductEntity` (`products`): `name`, `description`, `price`, `quantity`, `category`, `brand`, `expirationDate`,
  `@ManyToOne` `provider` and `@ManyToOne` `user`.
* `ProviderEntity` (`provider`): `cif`, `name` and `@OneToMany` `products`.
* `UserEntity` (`users`): `username`, unique case-insensitive `email` (stored in lower case), `createdAt` and
  `@OneToMany` `products`.

A product name is unique per provider, enforced both in the entity and in the database through
`uniqueConstraints = @UniqueConstraint(name = "uk_products_name_provider", columnNames = {"name", "provider_id"})`.
Violations are reported by the application as `409 Conflict`.

## Running the application

```bash
./mvnw spring-boot:run
```

The API listens on **port 8090** (`server.port=8090`). All configuration lives in
`src/main/resources/application.properties`; nothing is hardcoded in the Java sources.

H2 console: <http://localhost:8090/h2-console>

| Setting  | Value                                            |
|----------|--------------------------------------------------|
| JDBC URL | `jdbc:h2:file:./data/exam-db;DB_CLOSE_ON_EXIT=FALSE` |
| User     | `sa`                                             |
| Password | *(empty)*                                        |

The schema is recreated on every start (`spring.jpa.hibernate.ddl-auto=create-drop`) and seeded from
`src/main/resources/import.sql` with 3 providers, 5 users and 20 products.

## Endpoints

### `/api/v1/web/products` — Spring Web

| Method | Path                 | Description                                                            |
|--------|----------------------|------------------------------------------------------------------------|
| POST   | `/`                  | Uploads `Products.json` (multipart field `file`) and stores it (`201`). |
| GET    | `/`                  | Returns every persisted product.                                       |
| POST   | `/total-price`       | Total price of the products contained in the uploaded file.            |
| POST   | `/count-by-category` | Number of products of `?category=` contained in the uploaded file.     |

### `/api/v1/web/users` — Spring Web

| Method | Path | Description                                                         |
|--------|------|---------------------------------------------------------------------|
| POST   | `/`  | Uploads `Users.json` (multipart field `file`) and stores lower-case emails (`201`). |
| GET    | `/`  | Returns every persisted user.                                       |

### `/api/v1/query/products` — Spring Data JPA `@Query`

| Method | Path                    | Description                                                     |
|--------|-------------------------|-----------------------------------------------------------------|
| GET    | `/by-name`              | Products matching `?name=`.                                     |
| GET    | `/count-by-category`    | Number of products stored in `?category=`.                      |
| GET    | `/by-name-and-category` | Products matching `?name=` and `?category=`.                    |
| GET    | `/prices`               | Price of `?name=` for every provider, sorted ascending.         |
| GET    | `/lowest-price`         | Cheapest product for `?name=`, resolved with a `MIN` subquery.  |

### `/api/v1/custom/products` — Criteria API

| Method | Path                 | Description                                                                                                                      |
|--------|----------------------|----------------------------------------------------------------------------------------------------------------------------------|
| GET    | `/`                  | Optional `name` (partial, case insensitive) and `category`.                                                                      |
| GET    | `/price-lower-than`  | Products cheaper than `?price=`.                                                                                                 |
| GET    | `/expiration-range`  | Products expiring between `?startDate=` and `?endDate=`, sorted by descending id.                                                |
| GET    | `/by-user`           | Products of `?email=` (case insensitive), optionally filtered by `category` and `brand`.                                      |
| GET    | `/oldest-users`      | Products belonging to the `?users=` oldest users.                                                                                |
| GET    | `/search`            | Dynamic search by `name`, `category`, `brand`, `minPrice`, `maxPrice`, `startDate`, `endDate`, `providerId` returning a `Page`; paginated and sorted with `page`, `size`, `sortBy`, `direction`. |

### Error handling

`GlobalExceptionHandler` translates every failure into a `CustomError` payload (`message`, `timestamp`):

| Exception                                              | Status |
|--------------------------------------------------------|--------|
| `NotFoundException`                                    | 404    |
| `AlreadyExistsException`                               | 409    |
| `InvalidFileException`                                 | 400    |
| `MethodArgumentNotValidException`                      | 400    |
| `ConstraintViolationException`, `IllegalArgumentException` | 400 |

## Postman

Import [`collection.json`](collection.json) and set the `baseUrl` variable (defaults to `http://localhost:8090`).
The upload requests expect `src/main/resources/Products.json` and `src/main/resources/Users.json` to be selected in
the `file` form-data field.

## Tests

```bash
./mvnw clean verify
```

58 tests cover:

* `JsonFileReader`: valid files, empty files, malformed JSON and Bean Validation violations.
* Services (Mockito): relation resolution, duplicate detection inside and against the database, total price,
  category counting and the guard clauses of the Criteria API service.
* `ProductRepositoryTest` (`@DataJpaTest`): seed data, the unique `(name, provider)` constraint, every `@Query`
  method and every Criteria API query including pagination and sorting.
* MockMvc integration tests for the four controllers: uploads, listings, aggregations, validation errors and the
  `400`/`404`/`409` responses produced by the exception handler.

## Sample requests

```bash
curl -F "file=@src/main/resources/Users.json" http://localhost:8090/api/v1/web/users
curl -F "file=@src/main/resources/Products.json" http://localhost:8090/api/v1/web/products
curl -F "file=@src/main/resources/Products.json" http://localhost:8090/api/v1/web/products/total-price
curl "http://localhost:8090/api/v1/query/products/by-name?name=Port%C3%A1til"
curl "http://localhost:8090/api/v1/custom/products/search?category=Electr%C3%B3nica&size=3&sortBy=price&direction=DESC"
```
