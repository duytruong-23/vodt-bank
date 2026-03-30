DROP DATABASE IF EXISTS vodtbank;
CREATE DATABASE vodtbank;
USE vodtbank;

DROP TABLE IF EXISTS roles;
CREATE TABLE roles
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id                  SERIAL PRIMARY KEY,
    first_name          VARCHAR(255) NOT NULL,
    last_name           VARCHAR(255) NOT NULL,
    phone_number        VARCHAR(20)  NOT NULL UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    profile_picture_url VARCHAR(255),
    active              BOOLEAN   DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS users_roles;
CREATE TABLE users_roles
(
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS password_reset_codes;
CREATE TABLE password_reset_codes(
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(255) NOT NULL UNIQUE,
    expiration_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts
(
    id             SERIAL PRIMARY KEY,
    user_id        BIGINT UNSIGNED NOT NULL,
    account_number VARCHAR(20)     NOT NULL UNIQUE,
    balance        DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,
    account_type   VARCHAR(50)     NOT NULL,
    currency       VARCHAR(10)     NOT NULL,
    status         VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    closed_at      TIMESTAMP,
    created_at     TIMESTAMP                DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS transactions;
CREATE TABLE transactions
(
    id               SERIAL PRIMARY KEY,
    transaction_id      VARCHAR(50)    NOT NULL UNIQUE,
    account_id       BIGINT UNSIGNED NOT NULL,
    amount              DECIMAL(19, 2) NOT NULL,
    transaction_type VARCHAR(50)     NOT NULL,
    transaction_status  VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    description      TEXT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    source_account      VARCHAR(20),
    destination_account VARCHAR(20),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);