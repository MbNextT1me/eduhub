package ru.gormikle.eduhub.mapper;

import org.mapstruct.Mapper;
import ru.gormikle.eduhub.dto.FileDto;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.mapper.basic.BaseMapper;
import ru.gormikle.eduhub.mapper.basic.MappingConfig;

@Mapper(config = MappingConfig.class)
public interface FileMapper extends BaseMapper<FileDto, File> {
}
