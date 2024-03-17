package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gormikle.eduhub.entity.Cluster;
import ru.gormikle.eduhub.service.ClusterService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClusterController {
    private final ClusterService clusterService;

    @GetMapping("/clusters")
    public ResponseEntity<List<Cluster>> getAllClusters() {
        List<Cluster> clusters = clusterService.getAllClusters();
        return ResponseEntity.ok(clusters);
    }

    @GetMapping("/clusters/{id}")
    public ResponseEntity<Cluster> getClusterById(@PathVariable UUID id) {
        Cluster cluster = clusterService.getClusterById(id);
        return ResponseEntity.ok(cluster);
    }

    @PostMapping("/clusters")
    public ResponseEntity<Cluster> createCluster(@RequestBody Cluster cluster) {
        Cluster savedCluster = clusterService.saveCluster(cluster);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCluster);
    }

    @DeleteMapping("/clusters/{id}")
    public ResponseEntity<?> deleteCluster(@PathVariable UUID id) {
        clusterService.deleteCluster(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clusters/remoteExecution")
    public ResponseEntity<String> remoteExecution(@RequestParam("fileId") UUID fileId,
                                                  @RequestParam("taskId") UUID taskId,
                                                  @RequestParam("clusterId") UUID clusterId) {
        String result = clusterService.executeRemoteCode(fileId, taskId, clusterId);
        return ResponseEntity.ok(result);
    }
}
