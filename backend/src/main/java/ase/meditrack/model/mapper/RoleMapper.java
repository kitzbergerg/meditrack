package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Named("toDto")
    default RoleDto toDto(Role role) {
        return new RoleDto(
                role.getId(),
                role.getName(),
                role.getUsers() != null ? role.getUsers().stream().map(User::getId).toList() : null
        );
    }

    default Role fromDto(RoleDto dto) {
        Role role = new Role();

        if (dto.id() == null) {
            // id is only null on creation
            // userRepresentation.setEnabled(true);
        } else {
            role.setId(dto.id());
        }
        role.setName(dto.name());

        if (dto.users() != null) {
            role.setUsers(dto.users().stream().map(id -> {
                User user = new User();
                user.setId(id);
                return user;
            }).toList());
        }

        return role;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<RoleDto> toDtoList(List<Role> roles);
}
