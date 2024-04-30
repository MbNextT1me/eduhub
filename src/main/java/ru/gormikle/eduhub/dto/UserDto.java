package ru.gormikle.eduhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.gormikle.eduhub.entity.Role;

@AllArgsConstructor
@Data
public class UserDto {
    private String id;
    private String name;
    private String surname;
    private String email;
    private Role role;
}
