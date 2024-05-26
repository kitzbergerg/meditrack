package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.entity.HardConstraints;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(uses = EntityUuidMapper.class)
public interface HardConstraintsMapper {
    @Mapping(target = "shiftOffShift", ignore = true)
    HardConstraintsDto toDto(HardConstraints hardConstraints);

    @Mapping(target = "shiftOffShift", ignore = true)
    @Mapping(target = "team", ignore = true)
    HardConstraints fromDto(HardConstraintsDto dto);
}
