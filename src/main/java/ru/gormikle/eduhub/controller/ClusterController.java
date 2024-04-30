package ru.gormikle.eduhub.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gormikle.eduhub.dto.ClusterDto;
import ru.gormikle.eduhub.service.ClusterService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClusterController {

    private final ClusterService clusterService;

    public ClusterController(ClusterService clusterService){this.clusterService = clusterService;}

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

    @PostMapping("/clusters/remoteExecution")
    public ResponseEntity<String> remoteExecution(@RequestParam("fileId") String fileId,
                                                  @RequestParam("taskId") String taskId,
                                                  @RequestParam("clusterId") String clusterId) {
        String result = clusterService.executeRemoteCode(fileId, taskId, clusterId);
        return ResponseEntity.ok(result);
    }
}
