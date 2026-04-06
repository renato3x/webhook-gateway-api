CREATE TABLE endpoints (
    id SERIAL PRIMARY KEY,
    nickname VARCHAR(30) NOT NULL,
    url TEXT NOT NULL,
    user_id INTEGER NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)
