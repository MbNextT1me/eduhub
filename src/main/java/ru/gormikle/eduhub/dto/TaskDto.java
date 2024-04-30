package ru.gormikle.eduhub.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.gormikle.eduhub.dto.basic.BaseDto;
import ru.gormikle.eduhub.entity.File;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TaskDto extends BaseDto {
    private String name;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

    private String description;

    private List<File> files;
}
