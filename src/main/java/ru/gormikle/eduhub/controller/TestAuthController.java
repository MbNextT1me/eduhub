package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gormikle.eduhub.dto.JwtRequest;
import ru.gormikle.eduhub.dto.RegistrationUser;
import ru.gormikle.eduhub.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestAuthController {
    private final AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUser registrationUser) {
        return authService.createNewUser(registrationUser);
    }
}
