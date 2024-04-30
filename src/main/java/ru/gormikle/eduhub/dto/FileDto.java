package ru.gormikle.eduhub.dto;


import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.gormikle.eduhub.dto.basic.BaseDto;
import ru.gormikle.eduhub.entity.FileCategory;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FileDto extends BaseDto {

    private String name;

    private FileCategory category;
}
