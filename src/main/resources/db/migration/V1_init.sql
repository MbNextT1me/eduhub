CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL unique,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

insert into users(id,name,surname, email,password,role)
VALUES
    (gen_random_uuid (),'Админ', 'Админов', 'admin@eduhub.com','$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie', 'ROLE_ADMIN'),
    (gen_random_uuid (),'Миша', 'Горюнов', 'misha@eduhub.com', '$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie' , 'ROLE_STUDENT'),
    (gen_random_uuid (),'Мария', 'Александровна', 'maria@eduhub.com','$2a$12$jXIFqtfo3xQ5n7HhBNdIJOlG6GWdQggqb2HdWDDzg64/rQMuzOaie' , 'ROLE_TEACHER');
