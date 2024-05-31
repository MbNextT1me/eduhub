package ru.gormikle.eduhub.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gormikle.eduhub.dto.ClusterDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.service.ClusterService;
import ru.gormikle.eduhub.service.FileService;
import ru.gormikle.eduhub.service.TaskService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ClusterController {

    private final ClusterService clusterService;
    private final FileService fileService;

    private final TaskService taskService;
    public ClusterController(ClusterService clusterService, FileService fileService, TaskService taskService) {
        this.clusterService = clusterService;
        this.fileService = fileService;
        this.taskService = taskService;
    }

    @GetMapping("/clusters")
    public ResponseEntity<List<ClusterDto>> getAllClusters() {
        List<ClusterDto> clusters = clusterService.getAllClusters();
        return ResponseEntity.ok(clusters);
    }

    @GetMapping("/clusters/{id}")
    public ResponseEntity<ClusterDto> getClusterById(@PathVariable String id) {
        ClusterDto cluster = clusterService.getClusterById(id);
        return ResponseEntity.ok(cluster);
    }

    @PostMapping("/clusters")
    public ResponseEntity<ClusterDto> createCluster(@RequestBody ClusterDto cluster) {
        ClusterDto savedCluster = clusterService.saveCluster(cluster);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCluster);
    }

    @DeleteMapping("/clusters/{id}")
    public ResponseEntity<?> deleteCluster(@PathVariable String id) {
        clusterService.deleteCluster(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/clusters/{id}")
    public ResponseEntity<ClusterDto> updateCluster(@PathVariable String id, @RequestBody ClusterDto clusterDto) {
        clusterDto.setId(id);
        ClusterDto updatedCluster = clusterService.updateCluster(clusterDto);
        return ResponseEntity.ok(updatedCluster);
    }

    @PostMapping("/clusters/remoteExecution")
    public ResponseEntity<?> remoteExecution(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("taskId") String taskId,
                                                  HttpServletRequest request) throws IOException {
        File newFile = fileService.uploadFile(file, FileCategory.CLUSTER_SEND);
        taskService.addFileToTask(taskId, newFile.getId());
        CompletableFuture<String> executionResultFuture = clusterService.executeRemoteCode(newFile.getId(), taskId, request);
        String result = executionResultFuture.join();
        return ResponseEntity.ok().body(result);
    }


}
