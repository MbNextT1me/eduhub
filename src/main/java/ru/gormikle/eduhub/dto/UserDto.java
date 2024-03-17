package ru.gormikle.eduhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.gormikle.eduhub.entity.User;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UserDto {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private User.Role role;
}
