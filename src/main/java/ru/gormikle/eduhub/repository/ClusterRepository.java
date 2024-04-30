package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gormikle.eduhub.entity.Cluster;


public interface ClusterRepository extends JpaRepository<Cluster,String> {
}
