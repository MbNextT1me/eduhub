package ru.gormikle.eduhub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="clusters")
@Data
public class Cluster {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "hostName", nullable = false)
    private String hostName;

    @Column(name = "port", nullable = false)
    private String port;

    @Column(name = "hostUserName", nullable = false)
    private String hostUserName;

    @Column(name = "hostUserPassword", nullable = false)
    private String hostUserPassword;
}
