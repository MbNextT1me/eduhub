package ru.gormikle.eduhub.entity;

public enum Role {
    ROLE_ADMIN(1),
    ROLE_STUDENT(2),
    ROLE_TEACHER(3);
    private final int code;
    Role(int code) {
        this.code = code;
    }
}
