package ru.gormikle.eduhub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.dto.TaskDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.entity.Task;
import ru.gormikle.eduhub.mapper.TaskMapper;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.repository.TaskRepository;
import ru.gormikle.eduhub.service.basic.BaseMappedService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService extends BaseMappedService<Task, TaskDto,String,TaskRepository, TaskMapper> {

    private final FileService fileService;
    private final FileRepository fileRepository;

    public TaskService(TaskRepository repository, TaskMapper mapper, FileService fileService,FileRepository fileRepository) {
        super(repository,mapper);
        this.fileService = fileService;
        this.fileRepository = fileRepository;
    }

    public List<TaskDto> getAllTasks() {
        return getAllAsDto();
    }

    public TaskDto getTaskById(String id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        return toDto(task);
    }

    public TaskDto createTask(TaskDto taskDto) {
        return create(taskDto);
    }

    public TaskDto updateTask(String id, TaskDto taskDto) {
        return update(taskDto);
    }

    @Transactional
    public void addFileToTask(String taskId, String fileId) {
        Task task = repository.findById(taskId).orElse(null);
        if (task != null) {
            File file = fileRepository.findById(fileId).orElse(null);
            if (file != null) {
                List<File> files = task.getFiles();
                if (!files.contains(file)) {
                    files.add(file);
                    task.setFiles(files);
                    repository.save(task);
                }
            }
        }
    }

    public List<File> findTaskFilesByCategory (String taskId,FileCategory category){
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        return task.getFiles().stream()
                .filter(file -> file.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<File> findTaskFilesByCreatedBy(String taskId,String username){
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

        return task.getFiles().stream()
                .filter(file -> file.getCreatedBy().equals(username))
                .collect(Collectors.toList());
    }

    public void deleteTask(String id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

        List<File> files = task.getFiles();

        for (File file : files) {
            fileRepository.deleteFileFromTask(id, file.getId());
            fileRepository.delete(file);
        }

        repository.delete(task);
    }

    public void deleteFileFromTask(String taskId, String fileId){
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + fileId));

        task.getFiles().remove(file);
        repository.save(task);

        fileService.deleteFile(fileId);
    }
}
