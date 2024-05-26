package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.entity.HardConstraints;
import org.mapstruct.Mapper;


@Mapper(uses = EntityUuidMapper.class)
public interface HardConstraintsMapper {
    HardConstraintsDto toDto(HardConstraints hardConstraints);

    HardConstraints fromDto(HardConstraintsDto dto);
}
