package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.Task;
import ru.gormikle.eduhub.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/tasks/{taskId}/files/{fileId}")
    public ResponseEntity<?> addFileToTask(@PathVariable UUID taskId, @PathVariable UUID fileId) {
        taskService.addFileToTask(taskId, fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks/{taskId}/files/{category}")
    public ResponseEntity<List<File>> getFilesByCategory(@PathVariable UUID taskId, @PathVariable String category) {
        List<File> files = taskService.getFilesByCategory(taskId, category);
        return ResponseEntity.ok(files);
    }
}
