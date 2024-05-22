package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.dto.SimpleRoleDto;
import ase.meditrack.model.entity.Role;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface RoleMapper {

    @Named("toDto")
    RoleDto toDto(Role role);

    Role fromDto(RoleDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<RoleDto> toDtoList(List<Role> roles);

    @Mapping(target = "id", source = "role.id")
    @Mapping(target = "name", source = "role.name")
    SimpleRoleDto toSimpleDto(Role role);

}
