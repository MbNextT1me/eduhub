package ru.gormikle.eduhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gormikle.eduhub.dto.RegistrationUser;
import ru.gormikle.eduhub.entity.Role;
import ru.gormikle.eduhub.entity.User;
import ru.gormikle.eduhub.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllByRole(Role role){return userRepository.findAllByRole(role);}

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByEmail(username).orElseThrow(()->new UsernameNotFoundException(String.format("Пользователь '%s' не найден",username)));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }

    @Transactional
    public void deleteUserById(String userId) {
        userRepository.deleteById(userId);
    }


    public User createNewUser(RegistrationUser registrationUser){
        User user = new User();
        user.setEmail(registrationUser.getEmail());
        user.setName(registrationUser.getName());
        user.setSurname(registrationUser.getSurname());
        user.setPassword(passwordEncoder.encode(registrationUser.getPassword()));
        user.setRole(registrationUser.getRole());
        return userRepository.save(user);
    }
}
