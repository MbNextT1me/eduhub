package ru.gormikle.eduhub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.entity.User;
import ru.gormikle.eduhub.mapper.FileMapper;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.repository.UserRepository;
import ru.gormikle.eduhub.service.basic.BaseMappedService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class FileService extends BaseMappedService<File,FileDto,String,FileRepository, FileMapper> {

    @Value("${file.path}")
    private String fileStoragePath;


    public FileService(FileRepository repository, FileMapper mapper) {
        super(repository,mapper);
    }

    public List<File> findFilesByCategory(FileCategory category) {
        return repository.findAllByCategory(category);
    }


    public File uploadFile(MultipartFile file, FileCategory category) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (fileName.contains("..")) {
            throw new IllegalArgumentException("File name contains invalid path sequence");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Path userDirectory = Paths.get(fileStoragePath, "user_"+ username);
        Files.createDirectories(userDirectory);

        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setCategory(category);
        fileEntity.setCreatedBy(username);
        fileEntity = repository.save(fileEntity);

        String newFileName = fileEntity.getId() + "_" + fileName;
        Path filePath = userDirectory.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileEntity;
    }

    public Resource downloadFile(String fileId) {
        File file = repository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found"));
        Path filePath = Paths.get(fileStoragePath, "user_" + file.getCreatedBy(), file.getId() + "_" + file.getName());
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
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (fileName.contains("..")) {
                throw new IllegalArgumentException("File name contains invalid path sequence");
            }

            Path existingFilePath = Paths.get(fileStoragePath + existingFile.getName());
            Files.deleteIfExists(existingFilePath);

            Path filePath = Paths.get(fileStoragePath + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            existingFile.setName(fileName);
        }

        repository.save(existingFile);
    }
    public void deleteFile(String fileId) {
        File existingFile = repository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        Path filePath = Paths.get(fileStoragePath, "user_" + existingFile.getCreatedBy(), existingFile.getId() + "_" + existingFile.getName());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from storage", e);
        }
        repository.delete(existingFile);
    }
}
