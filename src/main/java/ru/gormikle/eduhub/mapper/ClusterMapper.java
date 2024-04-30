package ru.gormikle.eduhub.mapper;

import org.mapstruct.Mapper;
import ru.gormikle.eduhub.dto.ClusterDto;
import ru.gormikle.eduhub.entity.Cluster;
import ru.gormikle.eduhub.mapper.basic.BaseMapper;
import ru.gormikle.eduhub.mapper.basic.MappingConfig;

@Mapper(config = MappingConfig.class)
public interface ClusterMapper extends BaseMapper<ClusterDto,Cluster> {
}
