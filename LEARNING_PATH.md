# Spring Boot Learning Path
### From Zero to Production — Follow This Order

---

## How to Use This Guide

1. Read the file listed under **"Open This File"**
2. Run the app and hit the listed API endpoint to see it live
3. Check the **What to observe** note — know what to look for in the console/response
4. Move to the next topic only after the current one is clear

> App runs on: `http://localhost:8082`  
> H2 Console: `http://localhost:8082/h2-console` (JDBC URL: `jdbc:h2:mem:shopdb`)

---

## Stage 1 — Spring Core Fundamentals
> *Understand how Spring manages objects before writing any web code*

---

### 1.1 — What is IoC and Dependency Injection?
| | |
|---|---|
| **Open this file** | `config/DIConceptsDemo.java` |
| **Read sections** | Top comment block → `NotificationService` interface → `@Bean` methods |
| **Key question** | Why don't we write `new EmailNotificationService()`? Who creates it? |

**Concepts learned:**
- IoC Container (Spring creates and manages objects)
- `@Configuration` + `@Bean` — manual bean registration
- `@Primary` — default bean when multiple of same type exist
- `@Qualifier` — pick a specific bean by name
- 3 injection styles: constructor / setter / field

---

### 1.2 — Auto-Detection with Stereotypes
| | |
|---|---|
| **Open this file** | `service/impl/ProductServiceImpl.java` |
| **Read sections** | Class-level annotations, constructor |
| **Key question** | How does Spring know to create `ProductServiceImpl` without a `@Bean` method? |

**Concepts learned:**
- `@Service`, `@Repository`, `@Component` — Spring auto-detects these via `@ComponentScan`
- Constructor injection (recommended pattern)
- `@Transactional` — what it does and when to use `readOnly = true`

---

### 1.3 — Bean Scopes
| | |
|---|---|
| **Open this file** | `config/DIConceptsDemo.java` |
| **Read sections** | `CartService` (singleton) and `RequestContext` (prototype) `@Bean` methods |
| **Key question** | Run the app — how many times is `CartService` constructor printed? |

**Concepts learned:**
- `singleton` — one shared instance (default)
- `prototype` — new instance every injection
- When to use each

---

### 1.4 — Configuration Properties
| | |
|---|---|
| **Open this file** | `config/AppProperties.java` then `resources/application.properties` (bottom) |
| **Then open** | `controller/InfoController.java` |
| **Hit endpoint** | `GET http://localhost:8082/api/info` |
| **Key question** | What are the 3 ways to read a property? Which is best and why? |

**Concepts learned:**
- `@ConfigurationProperties(prefix="app")` — type-safe grouped config
- `@Value("${key}")` — single value injection
- `Environment` — programmatic property access
- `application.properties` structure

---

## Stage 2 — Database Layer (JPA)
> *How Spring talks to a database without writing SQL*

---

### 2.1 — Mapping a Class to a Database Table
| | |
|---|---|
| **Open this file** | `model/Product.java` |
| **Then open** | `model/Order.java` |
| **Key question** | Run the app, open H2 Console — do you see `PRODUCTS` and `ORDERS` tables? |

**Concepts learned:**
- `@Entity`, `@Table` — class becomes a DB table
- `@Id`, `@GeneratedValue` — auto-increment primary key
- `@Column` — customize column constraints
- `@ManyToOne`, `@JoinColumn` — foreign key relationship
- `@Enumerated(EnumType.STRING)` — store enum as text

---

### 2.2 — Querying the Database (Zero SQL)
| | |
|---|---|
| **Open this file** | `repository/ProductRepository.java` |
| **Then open** | `repository/OrderRepository.java` |
| **Hit endpoint** | `GET http://localhost:8082/api/products/category/Electronics` |
| **Key question** | How does `findByCategory()` work with no implementation written? |

**Concepts learned:**
- `JpaRepository<Entity, ID>` — free CRUD methods
- Query method naming: `findBy`, `findByXAndY`, `findByXLessThan`
- `@Query` — custom JPQL when naming isn't enough
- `nativeQuery = true` — raw SQL option

---

### 2.3 — Transactions
| | |
|---|---|
| **Open this file** | `service/impl/OrderServiceImpl.java` → `placeOrder()` method |
| **Key question** | If saving the order fails after deducting stock, what happens to stock? |

**Concepts learned:**
- `@Transactional` — wraps multiple DB operations in one atomic unit
- COMMIT on success, ROLLBACK on exception
- Why `placeOrder()` needs a transaction (stock + order are two operations)

---

## Stage 3 — REST API Layer
> *Exposing your data as HTTP endpoints*

---

### 3.1 — Building REST Endpoints
| | |
|---|---|
| **Open this file** | `controller/ProductController.java` |
| **Hit endpoints** | All endpoints in Postman → `Products` folder |
| **Key question** | What is the difference between `@PathVariable` and `@RequestParam`? |

**Concepts learned:**
- `@RestController` = `@Controller` + `@ResponseBody`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`
- `@PathVariable` — from URL: `/products/{id}`
- `@RequestParam` — from query string: `/products?name=iPhone`
- `@RequestBody` — from JSON body
- `ResponseEntity<T>` — control status code (200, 201, 404...)

---

### 3.2 — Input Validation
| | |
|---|---|
| **Open this file** | `dto/ProductRequest.java` |
| **Then open** | `exception/GlobalExceptionHandler.java` → `handleValidationErrors()` |
| **Hit endpoint** | `POST /api/products` with blank `name` field (see Postman: "Validation Error") |
| **Key question** | What annotation in the controller triggers the validation? |

**Concepts learned:**
- DTO (Data Transfer Object) vs Entity — why they're separate
- `@NotBlank`, `@Size`, `@Positive`, `@Min`, `@Max` — constraint annotations
- `@Valid` on `@RequestBody` — triggers validation
- `MethodArgumentNotValidException` — thrown when validation fails

---

### 3.3 — Standard Response Format
| | |
|---|---|
| **Open this file** | `dto/ApiResponse.java` |
| **Key question** | How does every endpoint return the same `{ success, message, data }` structure? |

**Concepts learned:**
- Generic wrapper class `ApiResponse<T>`
- Static factory methods (`success()`, `error()`)
- `@JsonInclude(NON_NULL)` — skip null fields in JSON

---

## Stage 4 — Error Handling
> *One place to handle all exceptions across all controllers*

---

### 4.1 — Custom Exceptions + Global Handler
| | |
|---|---|
| **Open this file** | `exception/ResourceNotFoundException.java` |
| **Then open** | `exception/GlobalExceptionHandler.java` |
| **Hit endpoint** | `GET http://localhost:8082/api/products/9999` |
| **Key question** | Without `@RestControllerAdvice`, where would you put the try-catch? |

**Concepts learned:**
- `@ResponseStatus(HttpStatus.NOT_FOUND)` on custom exceptions
- `@RestControllerAdvice` — intercepts exceptions from ALL controllers
- `@ExceptionHandler(XyzException.class)` — handles a specific exception type
- Exception hierarchy: 404, 400, 500 handlers

---

## Stage 5 — Cross-Cutting Concerns (AOP)
> *Logic that applies to many methods — written once, applied everywhere*

---

### 5.1 — Aspect Oriented Programming
| | |
|---|---|
| **Open this file** | `aspect/LoggingAspect.java` |
| **Hit any endpoint** | Watch the IntelliJ console output |
| **Key question** | Which code in `ProductServiceImpl` prints the `[AOP @Before]` line? (answer: none!) |

**Concepts learned:**
- Why AOP exists — removes cross-cutting boilerplate (logging, timing, security)
- `@Aspect`, `@Component` — declare an aspect
- Pointcut expression — `execution(* com.example..service.impl.*.*(..))` — which methods to intercept
- `@Before` — runs before the method
- `@After` — runs after (always, even on exception)
- `@AfterReturning` — runs after successful return, can inspect return value
- `@Around` — full control, must call `proceed()` to run the real method

---

## Stage 6 — Spring Events
> *Decouple actions that happen as a side effect of business logic*

---

### 6.1 — Publish and Listen to Events
| | |
|---|---|
| **Open this file** | `events/OrderPlacedEvent.java` then `events/OrderEventListener.java` |
| **Then open** | `service/impl/OrderServiceImpl.java` → `placeOrder()` |
| **Hit endpoint** | `POST http://localhost:8082/api/orders` (see Postman: "Place Order") |
| **Key question** | Does `OrderServiceImpl` know anything about email sending? |

**Concepts learned:**
- `ApplicationEvent` — base class for custom events
- `ApplicationEventPublisher.publishEvent()` — fire an event
- `@EventListener` — react to an event (Observer Pattern)
- Loose coupling — service fires event, listener does the work independently

---

## Stage 7 — Scheduling
> *Run tasks automatically on a timer*

---

### 7.1 — Scheduled Tasks
| | |
|---|---|
| **Open this file** | `scheduler/ReportScheduler.java` |
| **Observe** | Console — minute report prints every 60 seconds |
| **Key question** | What happens if a scheduled method throws an exception? |

**Concepts learned:**
- `@EnableScheduling` — activates scheduling (on main class)
- `@Scheduled(fixedRate=)` — every N milliseconds from app start
- `@Scheduled(fixedDelay=)` — N milliseconds after previous run finishes
- `@Scheduled(cron=)` — precise timing with cron expression
- `initialDelay` — wait before first run

---

## Stage 8 — Profiles
> *Different behaviour for dev, test, and production environments*

---

### 8.1 — Environment Profiles
| | |
|---|---|
| **Open this file** | `resources/application-dev.properties` and `application-prod.properties` |
| **Then open** | `config/AppConfig.java` — `@Profile("dev")` on `seedDemoData` |
| **Key question** | If you change `spring.profiles.active=prod`, will demo data still be seeded? |

**Concepts learned:**
- `spring.profiles.active=dev` — activate a profile
- `application-{profile}.properties` — overrides for that profile
- `@Profile("dev")` on a `@Bean` — bean only exists in that profile
- Dev vs Prod differences: H2 console, SQL logging, error details

---

## Stage 9 — Testing
> *Verify your code works without running the whole app manually*

---

### 9.1 — Unit Tests (fast, isolated)
| | |
|---|---|
| **Open this file** | `test/.../ProductServiceTest.java` |
| **Run** | Right-click → Run in IntelliJ |
| **Key question** | Does this test need a running database? Why not? |

**Concepts learned:**
- `@ExtendWith(MockitoExtension.class)` — activates Mockito (no Spring context)
- `@Mock` — fake object that returns what you tell it to
- `@InjectMocks` — real class with mocks injected
- `when().thenReturn()` — stub a return value
- `verify()` — assert a method was (or wasn't) called
- AssertJ: `assertThat().isEqualTo()`, `assertThatThrownBy()`

---

### 9.2 — Integration Tests (realistic, full stack)
| | |
|---|---|
| **Open this file** | `test/.../ProductControllerIntegrationTest.java` |
| **Run** | Right-click → Run in IntelliJ |
| **Key question** | What is the difference between this test and the unit test above? |

**Concepts learned:**
- `@SpringBootTest` — loads the full ApplicationContext
- `@AutoConfigureMockMvc` — simulates HTTP without a real server
- `MockMvc.perform()` — fire a fake HTTP request
- `jsonPath("$.data[0].name")` — assert JSON response fields
- `@Transactional` on test class — rolls back DB after each test

---

## Stage 10 — Production Monitoring (Actuator)
> *Built-in endpoints to monitor a live application*

---

### 10.1 — Actuator Endpoints
| | |
|---|---|
| **Open this file** | `resources/application.properties` → `management.*` section |
| **Hit endpoints** | Postman → `Actuator` folder |

| Endpoint | Shows |
|----------|-------|
| `/actuator/health` | App + DB status |
| `/actuator/info` | App name, version |
| `/actuator/metrics` | All metric names |
| `/actuator/metrics/jvm.memory.used` | JVM heap usage |
| `/actuator/beans` | Every bean in the context |
| `/actuator/env` | All properties and their values |

---

## Topics Still to Add (Stage 11+)

These are not yet in the project. Added in order of real-world importance:

| # | Topic | Key Annotations |
|---|-------|-----------------|
| 11 | **Bean Lifecycle** | `@PostConstruct`, `@PreDestroy` |
| 12 | **Pagination** | `Pageable`, `Page<T>`, `@PageableDefault` |
| 13 | **CORS** | `@CrossOrigin`, `WebMvcConfigurer` |
| 14 | **Async Methods** | `@Async`, `@EnableAsync`, `CompletableFuture` |
| 15 | **Calling External APIs** | `RestTemplate`, `WebClient` |
| 16 | **Caching** | `@Cacheable`, `@CacheEvict`, `@EnableCaching` |
| 17 | **JPA Auditing** | `@CreatedDate`, `@LastModifiedDate`, `@EnableJpaAuditing` |
| 18 | **Spring Security** | `SecurityFilterChain`, `BCryptPasswordEncoder`, JWT |
| 19 | **Test Slices** | `@WebMvcTest`, `@DataJpaTest`, `@MockBean` |
| 20 | **Filters & Interceptors** | `OncePerRequestFilter`, `HandlerInterceptor` |

---

## Quick Concept Lookup

| I want to understand... | Go to file |
|-------------------------|------------|
| How Spring creates objects | `DIConceptsDemo.java` |
| How to call the DB without SQL | `ProductRepository.java` |
| How to build a REST endpoint | `ProductController.java` |
| How to validate request input | `ProductRequest.java` + `GlobalExceptionHandler.java` |
| How to handle errors in one place | `GlobalExceptionHandler.java` |
| How to add logging without touching every method | `LoggingAspect.java` |
| How transactions work | `OrderServiceImpl.java` → `placeOrder()` |
| How to decouple side effects | `OrderPlacedEvent.java` + `OrderEventListener.java` |
| How to run code at startup | `AppConfig.java` → `seedDemoData()` |
| How to run code on a schedule | `ReportScheduler.java` |
| How to read from application.properties | `AppProperties.java` + `InfoController.java` |
| How to have different config per environment | `application-dev/prod.properties` |
| How to write a unit test | `ProductServiceTest.java` |
| How to write an integration test | `ProductControllerIntegrationTest.java` |
