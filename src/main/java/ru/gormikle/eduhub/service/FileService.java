package ru.gormikle.eduhub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.mapper.FileMapper;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.service.basic.BaseMappedService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class FileService extends BaseMappedService<File,FileDto,String,FileRepository, FileMapper> {

    @Value("${file.path}")
    private String fileStoragePath;

    public FileService(FileRepository repository, FileMapper mapper) {
        super(repository,mapper);
    }

    public void uploadFile(MultipartFile file, FileCategory category) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (fileName.contains("..")) {
            throw new IllegalArgumentException("File name contains invalid path sequence");
        }

        Path path = Paths.get(fileStoragePath + fileName);
        Files.copy(file.getInputStream(), path);

        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setCategory(category);
        repository.save(fileEntity);
    }

    public List<FileDto> getFilesByCategory(FileCategory category) {
        return repository.findAllByCategory(category);
    }

    public Resource downloadFile(String fileId) {
        File file = repository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found"));
        Path filePath = Paths.get(fileStoragePath + file.getName());
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or cannot be read");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found or cannot be read", e);
        }
    }
    public void updateFile(String fileId, MultipartFile file, FileCategory category) throws IOException {
        File existingFile = repository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        existingFile.setCategory(category);

        if (file != null && !file.isEmpty()) {
            // Если предоставлен новый файл, удаляем старый файл и сохраняем новый файл
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (fileName.contains("..")) {
                throw new IllegalArgumentException("File name contains invalid path sequence");
            }

            // Удаляем существующий файл из хранилища
            Path existingFilePath = Paths.get(fileStoragePath + existingFile.getName());
            Files.deleteIfExists(existingFilePath);

            // Сохраняем новый файл в хранилище
            Path filePath = Paths.get(fileStoragePath + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            existingFile.setName(fileName);
        }

        // Сохраняем обновленный файл в репозитории
        repository.save(existingFile);
    }
    public void deleteFile(String fileId) {
        File existingFile = repository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        repository.delete(existingFile);
    }
}
