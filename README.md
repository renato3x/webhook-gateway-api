# Webhook Gateway API

A backend service built with **Kotlin** and **Ktor** designed to act as a reliable intermediary for webhook delivery. The main goal is to ensure that data sent to third-party URLs is **never lost**, even if the destination server is temporarily offline. This project is also a practical study of **Resilient Distributed Systems** concepts such as non-blocking I/O, persistence-based reliability, and network resilience through retry strategies.

---

## Table of Contents

- [Purpose](#purpose)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Domain Models](#domain-models)
- [Value Objects](#value-objects)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Background Worker](#background-worker)
- [Error Handling](#error-handling)
- [Database](#database)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Planned Features](#planned-features)

---

## Purpose

Instead of making a client wait for an HTTP call to a third-party service to complete (which may fail, timeout, or be slow), this gateway:

1. Receives the request and immediately persists it as a **PENDING** delivery.
2. Returns `202 Accepted` to the caller right away.
3. A background worker picks up the pending delivery every minute and attempts to forward it to the target URL via HTTP POST.
4. If the delivery fails, the system retries using an **Exponential Backoff** strategy for up to 5 attempts before marking it as **FAILED**.

This follows the **Producer-Consumer** pattern, decoupling ingestion from execution entirely.

---

## Architecture

The project follows **Hexagonal Architecture** (also known as Ports & Adapters), organized around the **Dependency Rule**:

```
Infrastructure → Application → Domain
```

- **Domain** knows nothing about the outside world. It holds entities, domain exceptions, commands (intent objects), value objects, and port interfaces (contracts).
- **Application** implements the business logic via use cases. It only knows about the Domain layer — never about HTTP, databases, or frameworks.
- **Infrastructure** is the outermost layer and handles all technical details: Ktor HTTP routes, API Key authentication, Exposed ORM, Flyway migrations, Koin DI wiring, HikariCP connection pooling, DTOs, and the HTTP client for outgoing requests.

The `Application.kt` file acts as the **Composition Root**, where all concrete implementations are wired together through dependency injection.

```
src/main/kotlin/
├── Application.kt                     ← Composition Root / Ktor entry point
├── application/
│   └── usecase/                       ← Use case implementations (Port IN)
│       ├── CreateUserUseCaseImpl.kt
│       ├── CreateEndpointUseCaseImpl.kt
│       ├── CreateWebhookDeliveryUseCaseImpl.kt
│       └── DispatchWebhookUseCaseImpl.kt
├── domain/
│   ├── command/                       ← Intent objects passed to use cases
│   ├── dto/
│   │   └── HttpResponse.kt            ← Shared DTO for HTTP responses
│   ├── exception/                     ← Domain-specific exceptions
│   ├── model/                         ← Core business entities
│   ├── value/                         ← Domain value objects (e.g., Url)
│   └── port/
│       ├── in/                        ← Driving ports (use case interfaces)
│       └── out/                       ← Driven ports (repository/gateway interfaces)
│           ├── UserRepository.kt
│           ├── EndpointRepository.kt
│           ├── WebhookDeliveryRepository.kt
│           └── HttpRequestService.kt  ← Port for outgoing HTTP requests
└── infrastructure/
    ├── database/
    │   ├── DataSourceFactory.kt       ← HikariCP DataSource builder
    │   ├── FlywaySetup.kt             ← Flyway migration runner
    │   └── exposed/
    │       ├── ExposedDatabaseSetup.kt
    │       ├── repository/            ← Port OUT implementations (Exposed ORM)
    │       └── table/                 ← Exposed table definitions
    ├── di/
    │   └── AppModule.kt               ← Koin dependency injection module
    └── http/
        ├── dto/                       ← Request/Response DTOs + Validatable interface
        ├── exception/                 ← HTTP-layer exceptions
        ├── plugins/                   ← Ktor plugin configurations (including auth and worker)
        ├── principal/                 ← Ktor principal (UserPrincipal)
        ├── routes/                    ← Ktor route handlers
        └── service/
            └── KtorHttpRequestService.kt  ← HttpRequestService implementation (Ktor CIO client)
```

---

## Tech Stack

| Technology | Version | Role |
|---|---|---|
| Kotlin | 2.3.0 | Primary language |
| JVM | 21 | Runtime |
| Ktor (Netty) | 3.4.2 | HTTP server framework |
| Ktor CIO Client | 3.4.2 | HTTP client for outgoing webhook requests |
| Exposed | 1.2.0 | Kotlin ORM for SQL |
| PostgreSQL Driver | 42.7.8 | Database driver |
| HikariCP | 6.3.3 | JDBC connection pool |
| Flyway | 12.3.0 | Database schema migration |
| Koin | 4.2.0 | Dependency injection |
| kotlinx.serialization | 2.3.0 | JSON serialization |
| Logback | 1.4.14 | Structured logging |
| ktor-server-auth-api-key | 3.4.2 | API Key authentication plugin |
| MockK | 1.14.9 | Mocking library for tests |

---

## Project Structure

### Application Bootstrap

The `Application.module()` function is the Ktor entry point and bootstraps all plugins in order:

```kotlin
fun Application.module() {
    configureDI()             // 1. Koin dependency injection
    configureDatabases()      // 2. HikariCP + Flyway migrations + Exposed ORM
    configureSerialization()  // 3. JSON with snake_case naming strategy
    configureValidation()     // 4. Request body validation
    configureStatusPage()     // 5. Global exception → HTTP response mapping
    configureAuthentication() // 6. API Key authentication middleware
    configureWorkers()        // 7. Background worker coroutine
    configureRouting()        // 8. HTTP routes registration
}
```

### Dependency Injection (`AppModule.kt`)

Koin is used for IoC. The module wires interfaces to their concrete implementations:

```kotlin
val appModule = module {
    single<UserRepository>               { ExposedUserRepository() }
    single<CreateUserUseCase>            { CreateUserUseCaseImpl(get()) }

    single<EndpointRepository>           { ExposedEndpointRepository() }
    single<CreateEndpointUseCase>        { CreateEndpointUseCaseImpl(get(), get()) }

    single<WebhookDeliveryRepository>    { ExposedWebhookDeliveryRepository() }
    single<CreateWebhookDeliveryUseCase> { CreateWebhookDeliveryUseCaseImpl(get(), get()) }

    single<HttpRequestService>           { KtorHttpRequestService() }
    single<DispatchWebhookUseCase>       { DispatchWebhookUseCaseImpl(get(), get(), get()) }
}
```

### JSON Serialization

All JSON responses use **snake_case** naming strategy (e.g., `api_key` instead of `apiKey`, `user_id` instead of `userId`), configured via `kotlinx.serialization`.

### Request Validation

All request DTOs implement the `Validatable` interface:

```kotlin
interface Validatable {
    fun validate(): List<String>
}
```

Ktor's `RequestValidation` plugin intercepts every incoming body of type `Validatable`, calls `validate()`, and automatically returns `400 Bad Request` with the error list if any rule fails.

---

## Domain Models

### `User`
Represents a registered API user.

| Field | Type | Description |
|---|---|---|
| `id` | `Int?` | Auto-generated primary key |
| `username` | `String` | Unique username (5–16 chars, alphanumeric + underscores only) |
| `apiKey` | `Uuid` | Auto-generated UUID used for authentication |

### `Endpoint`
Represents a trusted destination URL registered by a user.

| Field | Type | Description |
|---|---|---|
| `id` | `Int?` | Auto-generated primary key |
| `url` | `Url` | The destination URL (wrapped in the `Url` value object) |
| `nickname` | `String` | A human-friendly label (6–30 characters) |
| `userId` | `Int` | Foreign key to `User` |

### `WebhookDelivery`
Represents a queued webhook dispatch attempt.

| Field | Type | Description |
|---|---|---|
| `id` | `Int?` | Auto-generated primary key |
| `endpointId` | `Int` | Foreign key to `Endpoint` |
| `payload` | `String` | The raw JSON payload to be forwarded |
| `attempts` | `Int` | Number of delivery attempts (starts at `0`) |
| `nextRetryAt` | `Instant?` | Timestamp for the next retry (Exponential Backoff) |
| `createdAt` | `Instant` | Timestamp of when the delivery was created (set automatically) |
| `status` | `WebhookDeliveryStatus` | `PENDING`, `SUCCESS`, or `FAILED` |

---

## Value Objects

### `Url`
An inline value class that wraps a `String` and enforces URL validity at the domain level. Validation rules:

- Must not be blank.
- Must be a syntactically valid URI (parseable by `java.net.URI`).
- Scheme must be `http` or `https`.
- Must have a non-blank host.
- Must **not** point to local addresses (`localhost` or `127.0.0.1`).

Any violation throws a `DomainException`, which is caught by the `CreateEndpointRequestDTO` validator and surfaced as a `400 Bad Request`.

---

## API Endpoints

All routes are prefixed with `/v1`.

### `GET /v1/ping`
Health check endpoint.

**Response:** `200 OK`
```
pong
```

---

### `POST /v1/users`
Registers a new user and returns an `api_key` for authentication.

**Request Body:**
```json
{
  "username": "myusername"
}
```

**Validation rules:**
- `username` must not be blank.
- `username` must not contain spaces.
- `username` must be between **5 and 16 characters**.
- `username` must only contain **letters, numbers, and underscores** (`[a-zA-Z0-9_]+`).

**Response:** `201 Created`
```json
{
  "api_key": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Error responses:**

| Status | Condition |
|---|---|
| `400 Bad Request` | Validation failed (blank username or invalid length) |
| `409 Conflict` | Username already exists |

---

### `POST /v1/endpoints` 🔒
Registers a trusted destination URL for the authenticated user.

> **Requires authentication.** Send the API key in the `X-API-Key` header.

**Request Body:**
```json
{
  "url": "https://example.com/webhook",
  "nickname": "My-Service"
}
```

**Validation rules:**
- `nickname` must not be blank.
- `nickname` must be between **6 and 30 characters**.
- `url` must be a valid `http` or `https` URL pointing to a non-local host.

**Response:** `201 Created`
```json
{
  "id": 1,
  "url": "https://example.com/webhook",
  "nickname": "My-Service",
  "user_id": 42
}
```

**Error responses:**

| Status | Condition |
|---|---|
| `400 Bad Request` | Missing/invalid `X-API-Key` header or validation failed |
| `404 Not Found` | Authenticated user not found |
| `409 Conflict` | This URL is already registered for this user |

---

### `POST /v1/dispatch` 🔒
Ingests a webhook payload for asynchronous delivery.

> **Requires authentication.** Send the API key in the `X-API-Key` header.

The endpoint immediately persists a `PENDING` `WebhookDelivery` record and returns `202 Accepted` without waiting for the actual HTTP delivery to the target URL.

**Request Body:**
```json
{
  "endpoint_id": 1,
  "payload": { "event": "user.created", "data": { "id": 99 } }
}
```

**Validation rules:**
- `endpoint_id` must be greater than `0`.
- `payload` must not be an empty JSON object (`{}`).

**Response:** `202 Accepted` *(empty body)*

**Business logic:**
1. Looks up the endpoint by `endpoint_id`. If not found, returns `404 Not Found`.
2. Verifies that the endpoint belongs to the authenticated user. If not, returns `403 Forbidden`.
3. Creates a `WebhookDelivery` with `status = PENDING`, `attempts = 0`, and `next_retry_at = null`.
4. Persists the delivery and returns `202 Accepted` immediately.

**Error responses:**

| Status | Condition |
|---|---|
| `400 Bad Request` | Missing/invalid `X-API-Key` header or validation failed |
| `403 Forbidden` | The `endpoint_id` does not belong to the authenticated user |
| `404 Not Found` | No endpoint found with the given `endpoint_id` |

---

## Authentication

Protected routes use **API Key authentication** via the `ktor-server-auth-api-key` plugin.

The API key must be sent in the **`X-API-Key`** request header:

```http
X-API-Key: 550e8400-e29b-41d4-a716-446655440000
```

**How it works:**
1. The middleware reads the `X-API-Key` header value.
2. It attempts to parse it as a `Uuid`. If parsing fails, it returns `400 Bad Request`.
3. It queries `UserRepository.findByApiKey()` for a matching user.
4. If a user is found, a `UserPrincipal(user)` is set as the call's principal and the request proceeds.
5. If no user is found, it returns `400 Bad Request` with a `"Missing or invalid API key"` error.

The `UserPrincipal` carries the full `User` domain object, making it available directly in protected route handlers via `call.principal<UserPrincipal>()`.

---

## Background Worker

The background worker is implemented as a Kotlin Coroutine launched on `Dispatchers.IO` when the application starts. It polls the database every **1 minute** for `PENDING` webhook deliveries and attempts to dispatch them.

### Worker lifecycle

```kotlin
monitor.subscribe(ApplicationStarted) {
    launch(Dispatchers.IO) {
        while (true) {
            dispatchWebhookUseCase.execute()
            delay(1.minutes)
        }
    }
}
```

### Dispatch logic (`DispatchWebhookUseCaseImpl`)

For each polling cycle:

1. Queries all `PENDING` deliveries from the database.
2. For each delivery:
   - If `attempts == 5` (max retries reached): marks the delivery as `FAILED`.
   - Otherwise: sends an HTTP POST request to the registered endpoint URL with the stored payload using `KtorHttpRequestService` (Ktor CIO client).
3. **On success** (HTTP 2xx): marks the delivery as `SUCCESS`.
4. **On failure** (HTTP 4xx/5xx or timeout):
   - Increments `attempts`.
   - Schedules the next retry using **Exponential Backoff**: $NextRetry = CurrentTime + (2^{attempts}\ minutes)$.

### Retry table

| Attempt | Delay before next retry |
|---|---|
| 1st failure | 1 minute |
| 2nd failure | 2 minutes |
| 3rd failure | 4 minutes |
| 4th failure | 8 minutes |
| 5th failure | marked as `FAILED` |

### `HttpRequestService` port

The domain defines an `HttpRequestService` port (driven port) that abstracts outgoing HTTP calls:

```kotlin
interface HttpRequestService {
    suspend fun post(url: String, body: Any, headers: Map<String, String> = emptyMap()): HttpResponse
}
```

The infrastructure provides `KtorHttpRequestService` as the concrete implementation using the Ktor CIO HTTP client.

---

## Error Handling

All errors are handled globally by Ktor's **StatusPages** plugin and return a consistent JSON structure:

```json
{
  "error": "Some fields are missing or invalid",
  "status_code": 400,
  "timestamp": "2026-04-05T12:00:00Z",
  "details": {
    "errors": ["username must not be blank"]
  }
}
```

| Exception | HTTP Status | Description |
|---|---|---|
| `UsernameAlreadyExistsException` | `409 Conflict` | Username is already taken |
| `EndpointAlreadyExistsException` | `409 Conflict` | URL already registered for this user |
| `UserNotFoundException` | `404 Not Found` | User lookup by ID failed |
| `EndpointNotFoundException` | `404 Not Found` | Endpoint lookup by ID failed |
| `UnauthorizedEndpointAccessException` | `403 Forbidden` | Endpoint does not belong to the authenticated user |
| `RequestValidationException` | `400 Bad Request` | Request body failed validation rules |
| `RequestException` | Configurable | Generic HTTP-layer error with custom status and details |
| `Throwable` (fallback) | `500 Internal Server Error` | Any unhandled exception |

---

## Database

The project uses **PostgreSQL** as its database, managed through **HikariCP** (connection pooling), **Flyway** (schema migrations), and **Exposed** (ORM).

### Schema migrations

Database schema is managed by **Flyway** and applied automatically on startup. Migration scripts live in `src/main/resources/db/migration/`:

| Migration | Description |
|---|---|
| `V1__create_users_table.sql` | Creates the `users` table with `id`, `username` (unique), and `api_key` (unique UUID) |
| `V2__create_endpoints_table.sql` | Creates the `endpoints` table with a cascade-delete FK to `users` |
| `V3__create_webhook_deliveries_table.sql` | Creates the `webhook_deliveries` table with status tracking and a cascade-delete FK to `endpoints` |

### Tables

- **`users`** — stores users with unique `username` and `api_key`.
- **`endpoints`** — stores allowed destination URLs with a cascade-delete FK to `users`.
- **`webhook_deliveries`** — stores queued delivery jobs with status, attempt count, and retry scheduling.

### Running the Database locally

A `docker-compose.yml` is provided for local development. Database credentials are loaded from an `.env.postgres` file:

```bash
docker compose up -d
```

This starts a **PostgreSQL 18** container with:
- **Host:** `localhost:5432`
- **Database:** configured via `.env.postgres`

Data is persisted via a named Docker volume (`database_vm`).

---

## Configuration

The application is configured through `src/main/resources/application.yaml`.

```yaml
ktor:
  application:
    modules:
      - dev.renato3x.ApplicationKt.module
  deployment:
    port: 8080
hikari:
  driverClassName: "org.postgresql.Driver"
  jdbcUrl: $DATABASE_URL
  username: $DATABASE_USER
  password: $DATABASE_PASSWORD
  maximumPoolSize: 6
```

Database credentials are read from environment variables:

| Variable | Description |
|---|---|
| `DATABASE_URL` | Full JDBC URL (e.g., `jdbc:postgresql://localhost:5432/webhook_gateway_app`) |
| `DATABASE_USER` | Database username |
| `DATABASE_PASSWORD` | Database password |

---

## Running the Project

### Prerequisites

- JDK 21+
- Docker (for the database)

### Steps

**1. Start the database:**
```bash
docker compose up -d
```

**2. Set the required environment variables:**
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/webhook_gateway_app
export DATABASE_USER=your_db_user
export DATABASE_PASSWORD=your_db_password
```

**3. Run the server:**
```bash
./gradlew run
```

The server will start on `http://0.0.0.0:8080`. Flyway will apply any pending migrations automatically on the first run.

### Other Gradle Tasks

| Task | Description |
|---|---|
| `./gradlew test` | Run the test suite |
| `./gradlew build` | Compile and build the project |
| `./gradlew buildFatJar` | Build a self-contained executable JAR |
| `./gradlew run` | Start the server in development mode |

---

## Planned Features

- **Structured Logging** — Full lifecycle logs tracking each delivery from ingestion through every attempt to final success or failure, replacing the current `println` statements in `DispatchWebhookUseCaseImpl`.
