package ru.gormikle.eduhub.service;

import com.jcraft.jsch.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import ru.gormikle.eduhub.utils.JwtTokenUtils;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@Slf4j
public class ClusterService extends BaseMappedService<Cluster, ClusterDto, String, ClusterRepository, ClusterMapper> {
    @Value("${jwt.prefix}")
    private String tokenPrefix;

    public ClusterService(ClusterRepository repository, ClusterMapper mapper, JwtTokenUtils jwtTokenUtils, FileRepository fileRepository, TaskRepository taskRepository, ClusterOperations clusterOperations) {
        super(repository, mapper);
        this.jwtTokenUtils = jwtTokenUtils;
        this.fileRepository = fileRepository;
        this.taskRepository = taskRepository;
        this.clusterOperations = clusterOperations;
    }

    private final JwtTokenUtils jwtTokenUtils;
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
        if (clusterDto.isUsedAsActive() && repository.existsByIsUsedAsActive(true)) {
            throw new IllegalArgumentException("There is already an active cluster.");
        }
        return create(clusterDto);
    }

    public ClusterDto updateCluster(ClusterDto clusterDto) {
        if (clusterDto.isUsedAsActive() && repository.existsByIsUsedAsActive(true)) {
            Optional<Cluster> currentActiveClusterOpt = repository.findByIsUsedAsActive(true);
            if (currentActiveClusterOpt.isPresent()) {
                Cluster currentActiveCluster = currentActiveClusterOpt.get();
                if (!currentActiveCluster.getId().equals(clusterDto.getId())) {
                    throw new IllegalArgumentException("There is already an active cluster.");
                }
            }
        }
        return update(clusterDto);
    }


    public void deleteCluster(String id) {
        repository.deleteById(id);
    }

    @Async
    public CompletableFuture<String> executeRemoteCode(String fileId, String taskId, HttpServletRequest request) {
        try {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + fileId));
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
            Cluster cluster = repository.findByIsUsedAsActive(true)
                    .orElseThrow(() -> new IllegalArgumentException("No active cluster found."));

            String authHeader = request.getHeader("Authorization");
            String token = authHeader.replace(tokenPrefix, "");
            String username = jwtTokenUtils.getUsername(token);

            Session session = clusterOperations.connectToCluster(cluster);

            clusterOperations.sendFileToCluster(session, file, taskId, username);
            clusterOperations.compileAndExecuteFile(session, file, taskId, username);

            session.disconnect();

            return CompletableFuture.completedFuture("Success. Execution log saved.");
        } catch (JSchException e) {
            return CompletableFuture.completedFuture("Error: " + e.getMessage());
        } catch (SftpException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

