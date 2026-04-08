# Webhook Gateway API

API built with **Kotlin + Ktor** to receive payloads and deliver webhooks in an asynchronous and resilient way.

## Overview

When an event is received, the API persists a delivery with status `PENDING` and immediately returns `202 Accepted`. A background worker tries to deliver the webhook with exponential retries up to a limit of 5 attempts.

Flow summary:
1. Receives `POST /v1/dispatch`.
2. Saves a pending delivery in the database.
3. Worker processes the queue every 1 minute.
4. On success: marks as `SUCCESS`.
5. On failure: schedules the next retry; after the limit, marks as `FAILED`.

## Recent changes in this repository

- Added `email` support on user creation (`email` is optional and unique when provided).
- Added authenticated route `GET /v1/users/me` to retrieve user data and endpoints.
- Webhook delivery now uses the `WebhookDispatcher` port with `KtorWebhookDispatcher` implementation.
- Added migration `V4__add_email_column_in_users_table.sql`.
- Error handling now includes `EmailAlreadyExistsException` with `409 Conflict`.

## Architecture

The project follows **Hexagonal Architecture (Ports & Adapters)**:

```text
Infrastructure -> Application -> Domain
```

- `domain`: entities, commands, business rules, exceptions, and ports.
- `application`: use case implementations.
- `infrastructure`: HTTP (Ktor), persistence (Exposed), DI (Koin), migrations (Flyway), and HTTP dispatcher.

`Application.kt` is the composition root and registers plugins in this order:

1. `configureDI()`
2. `configureDatabases()`
3. `configureSerialization()`
4. `configureValidation()`
5. `configureStatusPage()`
6. `configureAuthentication()`
7. `configureWorkers()`
8. `configureRouting()`

## Stack

Current versions (from `gradle.properties` and `build.gradle.kts`):

- Kotlin `2.3.0`
- Ktor `3.4.2`
- Exposed `1.2.0`
- PostgreSQL Driver `42.7.8`
- HikariCP `6.3.3`
- Flyway `12.3.0`
- Koin `4.2.0`
- MockK `1.14.9`
- JVM Toolchain `21`

## Endpoints

Base path: `/v1`

### `GET /v1/ping`
Health check.

Response `200`:

```text
pong
```

### `POST /v1/users`
Creates a user and returns an `api_key`.

Body:

```json
{
  "username": "myusername",
  "email": "user@example.com"
}
```

`email` is optional.

Validation rules currently implemented:
- `username` is required.
- `username` must not contain spaces.
- `username` length must be between 5 and 16 characters.
- `username` must only contain letters, numbers, and `_`.

Response `201`:

```json
{
  "api_key": "550e8400-e29b-41d4-a716-446655440000"
}
```

Common errors:
- `409` when username already exists.
- `409` when email already exists.
- `400` for invalid payload.

### `GET /v1/users/me` (authenticated)
Returns authenticated user data and endpoints.

Header:

```http
X-API-Key: 550e8400-e29b-41d4-a716-446655440000
```

Response `200`:

```json
{
  "id": 1,
  "username": "myusername",
  "email": "user@example.com",
  "endpoints": [
    {
      "id": 1,
      "url": "https://example.com/webhook",
      "nickname": "primary",
      "user_id": 1
    }
  ]
}
```

### `POST /v1/endpoints` (authenticated)
Registers an endpoint for the authenticated user.

Body:

```json
{
  "url": "https://example.com/webhook",
  "nickname": "My-Service"
}
```

Response `201`:

```json
{
  "id": 1,
  "url": "https://example.com/webhook",
  "nickname": "My-Service",
  "user_id": 1
}
```

### `POST /v1/dispatch` (authenticated)
Receives a payload and schedules asynchronous delivery.

Body:

```json
{
  "endpoint_id": 1,
  "payload": {
    "event": "user.created",
    "data": {
      "id": 99
    }
  }
}
```

Response: `202 Accepted` (empty body).

## Authentication

Protected routes use API Key via `X-API-Key` header.

- The key is parsed to `Uuid`.
- The user is fetched by `findByApiKey`.
- On authentication failure, it returns `400` with `Missing or invalid API key`.

## Dispatch worker

The worker runs in a coroutine (`Dispatchers.IO`) on startup and processes pending deliveries every 1 minute.

Retry behavior in `DispatchWebhookUseCaseImpl`:

- `maxRetries = 5`
- exponential delay: `2^attempts` minutes (1, 2, 4, 8)
- after max attempts: final status `FAILED`

HTTP sending is done through `WebhookDispatcher` with `KtorWebhookDispatcher` implementation.

## Errors and responses

Exceptions are centralized in `StatusPages` and return a standardized `ErrorResponseDTO`.

Main mappings:

- `UsernameAlreadyExistsException` -> `409`
- `EmailAlreadyExistsException` -> `409`
- `EndpointAlreadyExistsException` -> `409`
- `UserNotFoundException` -> `404`
- `EndpointNotFoundException` -> `404`
- `UnauthorizedEndpointAccessException` -> `403`
- `RequestValidationException` -> `400`
- fallback `Throwable` -> `500`

## Database

Database: PostgreSQL (with Exposed + HikariCP + Flyway).

Migrations in `src/main/resources/db/migration`:

- `V1__create_users_table.sql`
- `V2__create_endpoints_table.sql`
- `V3__create_webhook_deliveries_table.sql`
- `V4__add_email_column_in_users_table.sql`

## Configuration

File: `src/main/resources/application.yaml`

Required environment variables:

- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`

## Running locally

Prerequisites:

- JDK 21+
- Docker

1) Start database:

```bash
docker compose up -d
```

2) Export environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/webhook_gateway_app
export DATABASE_USER=your_db_user
export DATABASE_PASSWORD=your_db_password
```

3) Start API:

```bash
./gradlew run
```

Useful commands:

```bash
./gradlew test
./gradlew build
./gradlew buildFatJar
```

## Next steps

- Improve full delivery lifecycle logging (ingestion -> retries -> final status).
- **Email notifications will be implemented for users to inform whether a webhook was delivered successfully or, after all retry attempts are exhausted, failed definitively.**
