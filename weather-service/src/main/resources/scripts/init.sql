CREATE TABLE IF NOT EXISTS weather_history (
    id BIGSERIAL PRIMARY KEY,
    city VARCHAR(100) NOT NULL,
    query_date TIMESTAMPTZ NOT NULL,
    weather_response_json JSONB NOT NULL
);
