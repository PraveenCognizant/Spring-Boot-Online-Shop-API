# Spring Boot Complete Learning Project

> **Real-world scenario:** Online Shop with Products & Orders  
> Covers every major Spring Boot concept across **30 files** with educational comments.

---

## Quick Start

```bash
cd spring-core-demo
mvn spring-boot:run
```

| URL | Purpose |
|-----|---------|
| `http://localhost:8080/api/products`  | Products REST API (seeded with 10 items) |
| `http://localhost:8080/api/orders`    | Orders REST API |
| `http://localhost:8080/api/info`      | App config info |
| `http://localhost:8080/actuator/health` | Health check |
| `http://localhost:8080/h2-console`    | In-browser DB explorer (URL: `jdbc:h2:mem:shopdb`) |

Import `postman-collection.json` to test all endpoints instantly.

---

## File Map

### Entry Point

| File | Concept |
|------|---------|
| `SpringCoreDemoApplication.java` | `@SpringBootApplication`, `@EnableScheduling`, IoC container bootstrap |

---

### Model (`model/`)

| File | Concept |
|------|---------|
| `Product.java` | `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, validation annotations |
| `Order.java`   | `@ManyToOne`, `@JoinColumn`, `@Enumerated`, JPA relationships |

---

### Repository (`repository/`)

| File | Concept |
|------|---------|
| `ProductRepository.java` | `JpaRepository`, query method naming, `@Query`, native SQL |
| `OrderRepository.java`   | Derived queries on related entities, aggregation queries  |

---

### Service (`service/`)

| File | Concept |
|------|---------|
| `ProductService.java`     | Interface — program to abstractions, loose coupling                   |
| `ProductServiceImpl.java` | `@Service`, `@Transactional`, constructor injection, `Optional`       |
| `OrderService.java`       | Service interface for orders                                          |
| `OrderServiceImpl.java`   | Business rules, `ApplicationEventPublisher`, stock management         |

---

### Controller (`controller/`)

| File | Concept |
|------|---------|
| `ProductController.java` | `@RestController`, `@GetMapping/PostMapping/PutMapping/DeleteMapping`, `@PathVariable`, `@RequestParam`, `@Valid`, `ResponseEntity` |
| `OrderController.java`   | `@PatchMapping`, `@DeleteMapping`, Enum in path, `Map` request body  |
| `InfoController.java`    | `@Value`, `Environment`, `AppProperties` — 3 ways to read config     |

---

### DTO (`dto/`)

| File | Concept |
|------|---------|
| `ProductRequest.java` | DTO vs Entity, all Bean Validation annotations explained  |
| `ApiResponse.java`    | Generic response wrapper, `@JsonInclude`, static factory methods |

---

### Exception Handling (`exception/`)

| File | Concept |
|------|---------|
| `ResourceNotFoundException.java` | Custom `@ResponseStatus` exception (404)                    |
| `BadRequestException.java`       | Business rule violations (400)                              |
| `GlobalExceptionHandler.java`    | `@RestControllerAdvice`, `@ExceptionHandler`, validation error map |

---

### AOP (`aspect/`)

| File | Concept |
|------|---------|
| `LoggingAspect.java` | `@Aspect`, `@Before`, `@After`, `@AfterReturning`, `@Around`, pointcut expressions, execution timing |

---

### Events (`events/`)

| File | Concept |
|------|---------|
| `OrderPlacedEvent.java`    | Custom `ApplicationEvent` — Observer pattern    |
| `OrderEventListener.java`  | `@EventListener`, built-in `ContextRefreshedEvent` |

---

### Scheduler (`scheduler/`)

| File | Concept |
|------|---------|
| `ReportScheduler.java` | `@Scheduled` with `fixedRate`, `fixedDelay`, and `cron` expressions |

---

### Config (`config/`)

| File | Concept |
|------|---------|
| `AppConfig.java`       | `@Configuration`, `@Bean`, `CommandLineRunner`, `@Profile("dev")` seed data  |
| `AppProperties.java`   | `@ConfigurationProperties` — type-safe grouped config                        |
| `DIConceptsDemo.java`  | `@Primary`, `@Qualifier`, `@Scope`, all 3 injection types (constructor / setter / field) |

---

### Properties (`resources/`)

| File | Concept |
|------|---------|
| `application.properties`       | H2 DB, JPA, logging, Actuator, Jackson, custom `app.*` properties |
| `application-dev.properties`   | Dev profile overrides (verbose logging, detailed errors)           |
| `application-prod.properties`  | Prod profile — no console, no SQL output, validate-only schema     |

---

### Tests (`src/test/`)

| File | Concept |
|------|---------|
| `ProductServiceTest.java`              | Unit test: `@Mock`, `@InjectMocks`, `when/thenReturn`, `verify`, AssertJ |
| `ProductControllerIntegrationTest.java` | Integration test: `@SpringBootTest`, `MockMvc`, `jsonPath`, `@Transactional` rollback |

---

## API Reference

### Products

| Method   | Endpoint                                | Description              |
|----------|-----------------------------------------|--------------------------|
| `GET`    | `/api/products`                         | Get all products         |
| `GET`    | `/api/products/{id}`                    | Get product by ID        |
| `POST`   | `/api/products`                         | Create new product       |
| `PUT`    | `/api/products/{id}`                    | Update product           |
| `DELETE` | `/api/products/{id}`                    | Delete product           |
| `GET`    | `/api/products/category/{category}`     | Filter by category       |
| `GET`    | `/api/products/search?name=`            | Search by name           |
| `GET`    | `/api/products/price-range?min=&max=`   | Filter by price range    |
| `GET`    | `/api/products/low-stock?threshold=10`  | Low stock alert list     |

### Orders

| Method   | Endpoint                      | Description               |
|----------|-------------------------------|---------------------------|
| `GET`    | `/api/orders`                 | Get all orders            |
| `GET`    | `/api/orders/{id}`            | Get order by ID           |
| `POST`   | `/api/orders`                 | Place new order           |
| `PATCH`  | `/api/orders/{id}/status`     | Update order status       |
| `DELETE` | `/api/orders/{id}/cancel`     | Cancel order              |
| `GET`    | `/api/orders/status/{status}` | Filter by status          |
| `GET`    | `/api/orders/revenue`         | Get total revenue         |

### Other

| Method | Endpoint              | Description              |
|--------|-----------------------|--------------------------|
| `GET`  | `/api/info`           | App info & active profile |
| `GET`  | `/actuator/health`    | Health check             |
| `GET`  | `/actuator/info`      | App metadata             |
| `GET`  | `/actuator/metrics`   | Metrics list             |

---

## Concept Index

| Concept | Where to Look |
|---------|---------------|
| IoC / Dependency Injection  | `DIConceptsDemo.java`, `ProductServiceImpl.java` |
| REST API                    | `ProductController.java`, `OrderController.java` |
| JPA / Database              | `Product.java`, `ProductRepository.java` |
| Validation                  | `ProductRequest.java`, `GlobalExceptionHandler.java` |
| Exception Handling          | `exception/` package |
| AOP / Logging               | `LoggingAspect.java` |
| Events (Observer Pattern)   | `OrderPlacedEvent.java`, `OrderEventListener.java` |
| Scheduling                  | `ReportScheduler.java` |
| Configuration               | `AppProperties.java`, `application*.properties` |
| Profiles                    | `AppConfig.java`, `application-dev/prod.properties` |
| Unit Testing                | `ProductServiceTest.java` |
| Integration Testing         | `ProductControllerIntegrationTest.java` |
| Actuator                    | `application.properties` → `management.*` keys |
