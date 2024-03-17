package ru.gormikle.eduhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.Task;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.repository.TaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final FileRepository fileRepository;

    public List<Task> getAllTasks() {
        return (List<Task>) taskRepository.findAll();
    }

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(UUID id, Task task) {
        if (taskRepository.existsById(id)) {
            task.setId(id);
            return taskRepository.save(task);
        } else {
            return null;
        }
    }

    public void addFileToTask(UUID taskId, UUID fileId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            File file = fileRepository.findById(fileId).orElse(null);
            if (file != null) {
                List<File> files = task.getFiles();
                if (!files.contains(file)) {
                    files.add(file);
                    task.setFiles(files);
                    taskRepository.save(task);
                }
            }
        }
    }

    public List<File> getFilesByCategory(UUID taskId, String category) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            return task.getFiles().stream()
                    .filter(file -> file.getCategory().toString().equals(category))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
