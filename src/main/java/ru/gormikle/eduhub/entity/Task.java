package ru.gormikle.eduhub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import ru.gormikle.eduhub.entity.basic.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="tasks")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants
public class Task extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date_from", nullable = false)
    private LocalDateTime dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDateTime dateTo;

    @Column(name = "description")
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "task_files",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<File> files;
}


