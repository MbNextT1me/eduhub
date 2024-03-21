package ru.gormikle.eduhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.repository.FileRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    @Value("${file.path}")
    private String fileStoragePath;

    public void uploadFile(MultipartFile file, File.Category category) throws IOException {
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
        fileRepository.save(fileEntity);
    }

    public List<File> getFilesByCategory(File.Category category) {
        return fileRepository.findFilesByCategory(category);
    }

    public Resource downloadFile(UUID fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found"));
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
    public void updateFile(UUID fileId, MultipartFile file, File.Category category) throws IOException {
        File existingFile = fileRepository.findById(fileId)
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
        fileRepository.save(existingFile);
    }
    public void deleteFile(UUID fileId) {
        File existingFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        fileRepository.delete(existingFile);
    }
}
