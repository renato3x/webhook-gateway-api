CREATE TABLE webhook_deliveries (
    id SERIAL PRIMARY KEY,
    endpoint_id INTEGER NOT NULL,
    payload TEXT NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    next_retry_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    FOREIGN KEY (endpoint_id) REFERENCES endpoints(id) ON DELETE CASCADE
);
