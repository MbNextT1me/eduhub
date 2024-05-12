package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gormikle.eduhub.entity.Task;


public interface TaskRepository extends JpaRepository<Task, String> {
}
