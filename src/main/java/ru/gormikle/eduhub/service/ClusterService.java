package ru.gormikle.eduhub.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gormikle.eduhub.dto.ClusterDto;
import ru.gormikle.eduhub.entity.Cluster;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.Task;
import ru.gormikle.eduhub.mapper.ClusterMapper;
import ru.gormikle.eduhub.repository.ClusterRepository;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.repository.TaskRepository;
import ru.gormikle.eduhub.service.basic.BaseMappedService;
import ru.gormikle.eduhub.utils.ClusterOperations;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@Slf4j
public class ClusterService extends BaseMappedService<Cluster, ClusterDto,String,ClusterRepository, ClusterMapper> {
    public ClusterService(ClusterRepository repository, ClusterMapper mapper, FileRepository fileRepository, TaskRepository taskRepository, ClusterOperations clusterOperations){
        super(repository,mapper);
        this.fileRepository = fileRepository;
        this.taskRepository = taskRepository;
        this.clusterOperations = clusterOperations;
    }

    private final ClusterOperations clusterOperations;
    private final FileRepository fileRepository;
    private final TaskRepository taskRepository;
    public List<ClusterDto> getAllClusters() {
        return getAllAsDto();
    }

    public ClusterDto getClusterById(String id) {
        Cluster cluster = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cluster not found with id: " + id));
        return toDto(cluster);
    }

    public ClusterDto saveCluster(ClusterDto clusterDto) {
        return create(clusterDto);
    }

    public void deleteCluster(String id) {
        repository.deleteById(id);
    }

    @Async
    public CompletableFuture<String> executeRemoteCode(String fileId, String taskId, String clusterId) {
        try {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + fileId));
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
            Cluster cluster = repository.findById(clusterId)
                    .orElseThrow(() -> new IllegalArgumentException("Cluster not found with id: " + clusterId));

            Session session = clusterOperations.connectToCluster(cluster);
            clusterOperations.sendFileToCluster(session, file);
            clusterOperations.compileAndExecuteFile(session, file, task);
            session.disconnect();
            return CompletableFuture.completedFuture("Success. Execution log saved.");
        } catch (JSchException e) {
            return CompletableFuture.completedFuture("Error: " + e.getMessage());
        } catch (SftpException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
