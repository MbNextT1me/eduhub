package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gormikle.eduhub.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
