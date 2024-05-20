package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper
public interface RoleMapper {

    @Named("toDto")
    RoleDto toDto(Role role);

    default UUID userToId(User entity) {
        return entity != null ? entity.getId() : null;
    }

    Role fromDto(RoleDto dto);

    default User idToUser(UUID id) {
        User entity = new User();
        entity.setId(id);
        return entity;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<RoleDto> toDtoList(List<Role> roles);
}
