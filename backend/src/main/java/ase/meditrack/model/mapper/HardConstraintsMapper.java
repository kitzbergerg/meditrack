package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.entity.HardConstraints;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface HardConstraintsMapper {

    @Named("toDto")
    HardConstraintsDto toDto(HardConstraints entity);

    HardConstraints fromDto(HardConstraintsDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<HardConstraintsDto> toDtoList(List<HardConstraints> entityList);
}
