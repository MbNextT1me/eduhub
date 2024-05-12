package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.dto.TaskDto;
import ru.gormikle.eduhub.service.TaskService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController{

    private final TaskService taskService;

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

    @PostMapping("/tasks/files/")
    public ResponseEntity<?> addFileToTask(@RequestParam("taskId") String taskId, @RequestParam("fileId") String fileId) {
        taskService.addFileToTask(taskId, fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks/{taskId}/files/{category}")
    public ResponseEntity<List<FileDto>> getFilesByCategory(@PathVariable String taskId, @PathVariable String category) {
        List<FileDto> files = taskService.getFilesByCategory(taskId, category);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
