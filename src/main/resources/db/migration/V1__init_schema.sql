CREATE TABLE IF NOT EXISTS request_status (
    id SERIAL PRIMARY KEY,
    payload TEXT NOT NULL,
    status VARCHAR(20),
    details TEXT,
    created_at TIMESTAMP NOT NULL
);
