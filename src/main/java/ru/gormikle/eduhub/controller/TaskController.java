package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.dto.TaskDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.service.FileService;
import ru.gormikle.eduhub.service.TaskService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController{

    private final TaskService taskService;
    private final FileService fileService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable String id) {
        TaskDto task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable String id, @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/tasks/files")
    public ResponseEntity<?> addFileToTask(@RequestParam("taskId") String taskId, @RequestParam("file") MultipartFile file,@RequestParam("category") FileCategory category) throws IOException {
        File newFile = fileService.uploadFile(file,category);
        taskService.addFileToTask(taskId, newFile.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks/{taskId}/files/{category}")
    public ResponseEntity<?> getFilesByCategory(@PathVariable String taskId, @PathVariable FileCategory category) {
        List<File> files  = taskService.findTaskFilesByCategory(taskId,category);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/tasks/{taskId}/users/{username}")
    public ResponseEntity<?> getFilesByUsername(@PathVariable String taskId, @PathVariable String username) {
        List<File> files  = taskService.findTaskFilesByCreatedBy(taskId,username);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/tasks/{taskId}/files/{fileId}")
    public ResponseEntity<?> deleteFileFromTask(@PathVariable String taskId, @PathVariable String fileId) {
        taskService.deleteFileFromTask(taskId, fileId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
