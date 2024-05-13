package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;

import java.util.List;
import java.util.Optional;


public interface FileRepository extends JpaRepository<File, String> {
    @Query("SELECT f FROM File f WHERE f.category = :category")
    List<File> findAllByCategory(@Param("category") FileCategory category);

    @Modifying
    @Query(value = "DELETE FROM task_files WHERE task_id = CAST(:taskId AS UUID) AND file_id = CAST(:fileId AS UUID)", nativeQuery = true)
    void deleteFileFromTask(@Param("taskId") String taskId, @Param("fileId") String fileId);
}
