package ru.gormikle.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gormikle.eduhub.entity.Cluster;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.gormikle.eduhub.entity.Cluster;

import java.util.Optional;

public interface ClusterRepository extends JpaRepository<Cluster, String> {
    boolean existsByIsUsedAsActive(boolean isUsedAsActive);
    Optional<Cluster> findByIsUsedAsActive(boolean isUsedAsActive);
}

