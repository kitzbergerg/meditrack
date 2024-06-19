package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.dto.RoleHardConstraintsDto;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(uses = EntityUuidMapper.class)
public interface HardConstraintsMapper {
    HardConstraintsDto toDto(HardConstraints hardConstraints);

    HardConstraints fromDto(HardConstraintsDto dto);

    @Mapping(source = "id", target = "roleId")
    RoleHardConstraintsDto toRoleHardconstraintsDto(Role x);
}
