package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;

import java.util.List;
import java.util.UUID;


public interface FileRepository extends CrudRepository<File, UUID> {

    List<File> findAllByCategory(FileCategory category);
}
