package ru.gormikle.eduhub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import ru.gormikle.eduhub.entity.basic.BaseEntity;

@Entity
@Table(name="clusters")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants
public class Cluster extends BaseEntity {

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

    @Column(name = "isUsedAsActive", nullable = false)
    private boolean isUsedAsActive;
}
