package ru.gormikle.eduhub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import ru.gormikle.eduhub.entity.basic.BaseEntity;

@Entity
@Table(name="files")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants
public class File extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "category", nullable = false)
    private FileCategory category;

    @Column(name = "created_by", nullable = false)
    private String createdBy;
}
