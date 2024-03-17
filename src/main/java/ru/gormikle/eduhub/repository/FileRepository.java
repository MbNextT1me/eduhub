package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gormikle.eduhub.entity.File;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends CrudRepository<File, UUID> {


    @Query("SELECT f FROM File f WHERE f.category = :category")
    List<File> findFilesByCategory(@Param("category") File.Category category);
}
