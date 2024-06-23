package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.dto.RoleHardConstraintsDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(uses = EntityUuidMapper.class)
public interface HardConstraintsMapper {
    @Mapping(source = "daytimeRequiredPeople", target = "daytimeRequiredPeople")
    @Mapping(source = "nighttimeRequiredPeople", target = "nighttimeRequiredPeople")
    HardConstraintsDto toDto(Team team);

    @Mapping(source = "id", target = "roleId")
    RoleHardConstraintsDto toRoleHardconstraintsDto(Role x);
}
