package ru.gormikle.eduhub.mapper;

import org.mapstruct.Mapper;
import ru.gormikle.eduhub.dto.TaskDto;
import ru.gormikle.eduhub.entity.Task;
import ru.gormikle.eduhub.mapper.basic.BaseMapper;
import ru.gormikle.eduhub.mapper.basic.MappingConfig;

@Mapper(config = MappingConfig.class)
public interface TaskMapper extends BaseMapper<TaskDto, Task> {
}
