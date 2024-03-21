package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.service.FileService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("category") File.Category category) {
        try {
            fileService.uploadFile(file, category);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<File>> getFilesByCategory(@RequestParam("category") File.Category category) {
        List<File> files = fileService.getFilesByCategory(category);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID fileId) throws IOException {
        Resource fileResource = fileService.downloadFile(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

    @PutMapping("/files/{fileId}")
    public ResponseEntity<?> updateFile(@PathVariable UUID fileId,
                                        @RequestParam("file") MultipartFile file,
                                        @RequestParam("category") File.Category category) {
        try {
            fileService.updateFile(fileId, file, category);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable UUID fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
