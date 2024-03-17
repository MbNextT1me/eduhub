package ru.gormikle.eduhub.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gormikle.eduhub.entity.Cluster;

import java.util.UUID;

@Repository
public interface ClusterRepository extends CrudRepository<Cluster, UUID> {
}
