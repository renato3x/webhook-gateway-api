# Webhook Gateway API

A simplified backend service built with Kotlin to study **Resilient Distributed Systems**. It acts as a reliable intermediary, ensuring that data sent to third-party webhooks is delivered even if the destination server is temporarily offline.

---

## 1. System Overview
The project follows the **Producer-Consumer** pattern. Instead of making the user wait for a network request, the system saves the task and processes it in the background using Kotlin Coroutines.

---

## 2. Core Entities (Database Schema)

### User
* `id`: UUID (Primary Key)
* `username`: String
* `api_key`: String (Unique identifier for authentication)

### AllowedEndpoint
* `id`: UUID (Primary Key)
* `user_id`: UUID (Foreign Key)
* `url`: String (The validated destination URL)
* `nickname`: String (e.g., "Customer-API")

### WebhookDelivery
* `id`: UUID (Primary Key)
* `endpoint_id`: UUID (Foreign Key)
* `payload`: Text/JSON (The raw data to be sent)
* `status`: Enum (PENDING, SUCCESS, FAILED)
* `attempts`: Integer (Starting at 0)
* `next_retry_at`: Timestamp (When the worker should try again)

---

## 3. API Endpoints

### [POST] `/users`
Registers a new user and returns an `api_key`.

### [POST] `/endpoints`
Registers a URL that the user is allowed to call.
* **Requirement:** Must include `Authorization: Bearer <api_key>`.

### [POST] `/dispatch`
The main ingestion point.
* **Body:** `{ "endpoint_id": "...", "payload": { ... } }`
* **Logic:** 
  1. Validate if the `endpoint_id` belongs to the authenticated user.
  2. Create a `WebhookDelivery` record with status `PENDING`.
  3. Return `202 Accepted` immediately.

---

## 4. The Dispatch & Retry Logic (The "Worker")

The core of the project is a background routine (Worker) that manages network resilience.

### Step 1: The Poller
A continuous loop using `launch(Dispatchers.IO)` that queries the database every 10 seconds for deliveries where:
* `status == PENDING`
* `next_retry_at <= current_time`

### Step 2: The Execution
The worker uses `Ktor HttpClient` to send a `POST` request to the target URL with the stored payload.

### Step 3: The Result Handling
* **HTTP 2xx (Success):** Update status to `SUCCESS`.
* **HTTP 4xx/5xx or Timeout (Failure):**
    1. Increment `attempts`.
    2. If `attempts < 5`: 
        * Keep status as `PENDING`.
        * Schedule `next_retry_at` using **Exponential Backoff**: $NextRetry = CurrentTime + (2^{attempts} \text{ minutes})$.
    3. If `attempts >= 5`:
        * Update status to `FAILED`.
        * Log the final error for debugging.

---

## 5. Development Checklist

- [ ] **Infrastructure:** Setup Ktor Server with ContentNegotiation (JSON) and Exposed ORM.
- [ ] **Auth:** Implement a simple Header interceptor to validate the `api_key`.
- [ ] **Producer:** Build the `/dispatch` route to persist data without blocking the response.
- [ ] **Consumer:** Implement the `while(true)` Coroutine loop for processing the queue.
- [ ] **Backoff Algorithm:** Write a utility function to calculate the exponential delay between retries.
- [ ] **Logging:** Implement structured console logs to track the "life" of a webhook (Ingestion -> Attempt 1 -> Failure -> Attempt 2 -> Success).

---

## 6. Why this project?
By building this, you demonstrate knowledge in:
1. **Non-blocking I/O:** Handling requests without making the client wait.
2. **Persistence-based Reliability:** Data is never lost even if the app restarts.
3. **Network Resilience:** Implementing real-world strategies for dealing with unstable APIs.