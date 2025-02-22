-- liquibase formatted sql

-- changeset julia:1
CREATE TABLE user_data (
    uuid VARCHAR(255) PRIMARY KEY,
    money INT NOT NULL DEFAULT 0,
    country VARCHAR(3) NOT NULL,
    activity INT NOT NULL DEFAULT 0,
    sync_count INT DEFAULT 0,
    stat_count INT DEFAULT 0,
    last_sync_time BIGINT,
    last_stat_time BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_activity_history (
    id SERIAL PRIMARY KEY,
    uuid VARCHAR(255),
    activity INT,
    activity_date DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (uuid) REFERENCES user_data(uuid)
);

-- changeset julia:2
CREATE INDEX idx_country_money ON user_data (country, money DESC);
CREATE INDEX idx_country ON user_data (country);
CREATE INDEX idx_created_at ON user_data (created_at);
CREATE INDEX idx_user_activity ON user_activity_history (uuid, activity_date DESC);

-- changeset julia:3
ALTER TABLE user_data
ALTER COLUMN created_at DROP DEFAULT;