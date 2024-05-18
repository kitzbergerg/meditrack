package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class RoleMapper {

    @Named("toDto")
    public RoleDto toDto(Role role) {
        return new RoleDto(
                role.getId(),
                role.getName(),
                role.getColor(),
                role.getAbbreviation(),
                role.getUsers() != null ? role.getUsers().stream().map(User::getId).toList() : null
        );
    }

    public Role fromDto(RoleDto dto) {
        Role role = new Role();

        if (dto.id() == null) {
            // id is only null on creation
            // userRepresentation.setEnabled(true);
        } else {
            role.setId(dto.id());
        }
        role.setName(dto.name());

        if (dto.color() != null) {
            role.setColor(dto.color());
        }

        if (dto.abbreviation() != null) {
            role.setAbbreviation(dto.abbreviation());
        }

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
    public abstract List<RoleDto> toDtoList(List<Role> roles);
}
