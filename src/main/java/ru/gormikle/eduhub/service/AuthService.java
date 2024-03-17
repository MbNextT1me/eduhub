package ru.gormikle.eduhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.gormikle.eduhub.dto.JwtRequest;
import ru.gormikle.eduhub.dto.JwtResponse;
import ru.gormikle.eduhub.dto.RegistrationUser;
import ru.gormikle.eduhub.dto.UserDto;
import ru.gormikle.eduhub.entity.User;
import ru.gormikle.eduhub.exception.AppError;
import ru.gormikle.eduhub.utils.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    //обработку исключений надо мб как-то по-другому делать (ControllerAdvice)
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"),HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());

        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUser registrationUser){
        if (!registrationUser.getPassword().equals(registrationUser.getConfirmPassword())){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        }

        if (userService.findByEmail(registrationUser.getEmail()).isPresent()){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Пользователь уже существует"), HttpStatus.UNAUTHORIZED);
        }
        User user = userService.createNewUser(registrationUser);
        return ResponseEntity.ok(new UserDto(user.getId(),user.getEmail(), user.getSurname(),user.getName(),user.getRole()));
    }
}
