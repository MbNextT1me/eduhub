package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;

import java.util.List;


public interface FileRepository extends JpaRepository<File, String> {
    List<FileDto> findAllByCategory(FileCategory category);
}