CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL unique,
    password VARCHAR(255) NOT NULL,
    role SMALLINT NOT NULL
);

insert into users(id,name,surname, email,password,role)
VALUES
   (gen_random_uuid()::text,'Админ', 'Админов', 'admin@eduhub.com','$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie', 1),
   (gen_random_uuid()::text,'Миша', 'Горюнов', 'misha@eduhub.com', '$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie' , 2),
   (gen_random_uuid()::text,'Мария', 'Александровна', 'maria@eduhub.com','$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie' , 3);


CREATE TABLE IF NOT EXISTS files (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category SMALLINT NOT NULL
);

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
