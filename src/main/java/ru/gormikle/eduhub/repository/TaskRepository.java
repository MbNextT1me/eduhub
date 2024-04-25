package ru.gormikle.eduhub.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gormikle.eduhub.entity.Task;

import java.util.UUID;

public interface TaskRepository extends CrudRepository<Task, UUID> {}
