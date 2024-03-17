package ru.gormikle.eduhub.dto;

import lombok.Data;
import ru.gormikle.eduhub.entity.User;

@Data
public class RegistrationUser {

    private String name;

    private String surname;

    private String email;

    private String password;

    private String confirmPassword;

    private User.Role role;
}
