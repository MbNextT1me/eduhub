-- V2__Add_initial_active_cluster.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role SMALLINT NOT NULL
);

INSERT INTO users(id, name, surname, email, password, role)
VALUES
    (gen_random_uuid()::text, 'Админ', 'Админов', 'admin@eduhub.com', '$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie', 0),
    (gen_random_uuid()::text, 'Миша', 'Горюнов', 'misha@eduhub.com', '$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie', 1),
    (gen_random_uuid()::text, 'Мария', 'Александровна', 'maria@eduhub.com', '$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie', 2)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS files (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category SMALLINT NOT NULL
);

ALTER TABLE files ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);

CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date_from TIMESTAMP NOT NULL,
    date_to TIMESTAMP NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS task_files (
    task_id VARCHAR(255) NOT NULL,
    file_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (task_id, file_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (file_id) REFERENCES files(id)
);

CREATE TABLE IF NOT EXISTS clusters (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    host_name VARCHAR(255) NOT NULL,
    port VARCHAR(10) NOT NULL,
    host_user_name VARCHAR(255) NOT NULL,
    host_user_password VARCHAR(255) NOT NULL
);

ALTER TABLE clusters ADD COLUMN IF NOT EXISTS is_used_as_active BOOLEAN NOT NULL DEFAULT FALSE;

INSERT INTO clusters(id, name, host_name, port, host_user_name, host_user_password, is_used_as_active)
VALUES (gen_random_uuid()::text, 'main', '194.176.114.13', '22', 'michaelg', 'Laba403', TRUE);
