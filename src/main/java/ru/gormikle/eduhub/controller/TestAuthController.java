package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gormikle.eduhub.dto.JwtRequest;
import ru.gormikle.eduhub.dto.RegistrationUser;
import ru.gormikle.eduhub.dto.UserDto;
import ru.gormikle.eduhub.service.AuthService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class TestAuthController {
    private final AuthService authService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        List<UserDto> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        log.debug(String.valueOf(authRequest));
        return authService.createAuthToken(authRequest);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        authService.deleteUserById(userId);
        return ResponseEntity.ok("Пользователь успешно удален");
    }


    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUser registrationUser) {
        return authService.createNewUser(registrationUser);
    }
}
