package ru.gormikle.eduhub.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.gormikle.eduhub.dto.basic.BaseDto;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClusterDto extends BaseDto {
    private String name;

    private String hostName;

    private String port;

    private String hostUserName;

    private String hostUserPassword;

    private boolean isUsedAsActive;
}
