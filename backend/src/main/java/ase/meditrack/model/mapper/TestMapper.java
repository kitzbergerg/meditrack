package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.TestDto;
import ase.meditrack.model.entity.Test;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TestMapper {
    TestMapper MAPPER = Mappers.getMapper(TestMapper.class);

    @Mapping(source = "id", target = "id")
    TestDto toDto(Test entity);

    @InheritInverseConfiguration
    Test fromDto(TestDto dto);

    List<TestDto> toDtoList(List<Test> entityList);
}
