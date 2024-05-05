package ru.gormikle.eduhub.entity;

public enum Role {
    ROLE_ADMIN(0),
    ROLE_STUDENT(1),
    ROLE_TEACHER(2);
    private final int code;
    Role(int code) {
        this.code = code;
    }
}
