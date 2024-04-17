package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.TestDto;
import ase.meditrack.model.entity.Test;
import org.mapstruct.*;

import java.util.List;

@Mapper
public interface TestMapper {

    @Named("toDto")
    TestDto toDto(Test entity);

    Test fromDto(TestDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<TestDto> toDtoList(List<Test> entityList);
}
