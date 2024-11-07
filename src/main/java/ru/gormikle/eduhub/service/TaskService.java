package ru.gormikle.eduhub.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final FileRepository fileRepository;

    public TaskService(TaskRepository repository, TaskMapper mapper, FileRepository fileRepository) {
        super(repository,mapper);
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

    public List<FileDto> getFilesByCategory(String taskId, String category) {
        TaskDto taskDto = getTaskById(taskId);
        if (taskDto != null) {
            return taskDto.getFiles().stream()
                    .flatMap(fileDto -> fileRepository.findAllByCategory(FileCategory.valueOf(category)).stream())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
