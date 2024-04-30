package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gormikle.eduhub.entity.Task;


public interface TaskRepository extends JpaRepository<Task, String> {}
