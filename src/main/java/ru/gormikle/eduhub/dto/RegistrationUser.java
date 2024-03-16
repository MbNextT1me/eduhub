package ru.gormikle.eduhub.dto;

import lombok.Data;

@Data
public class RegistrationUser {

    private String name;

    private String surname;

    private String email;

    private String password;

    private String confirmPassword;
}
