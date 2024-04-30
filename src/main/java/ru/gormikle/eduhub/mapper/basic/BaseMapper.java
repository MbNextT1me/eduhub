package ru.gormikle.eduhub.mapper.basic;

public interface BaseMapper<DTO, ENTITY> {

    DTO toDto(ENTITY entity);

    ENTITY fromDto(DTO dto);
}
