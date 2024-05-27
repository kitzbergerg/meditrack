package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.User;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {EntityUuidMapper.class, RoleMapper.class})
public abstract class UserMapper {

    @Autowired
    private RealmResource meditrackRealm;

    @Named("toDto")
    @Mapping(target = "id", expression = "java(UUID.fromString(user.getUserRepresentation().getId()))")
    @Mapping(target = "username", expression = "java(user.getUserRepresentation().getUsername())")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", expression = "java(user.getUserRepresentation().getEmail())")
    @Mapping(target = "firstName", expression = "java(user.getUserRepresentation().getFirstName())")
    @Mapping(target = "lastName", expression = "java(user.getUserRepresentation().getLastName())")
    @Mapping(source = "user", target = "roles", qualifiedByName = "mapRoles")
    public abstract UserDto toDto(User user);

    @Named("mapRoles")
    protected List<String> mapRoles(User user) {
        // for some reason keycloak doesn't return the roles in UserRepresentation, so we need to fetch them manually
        return meditrackRealm.users()
                .get(user.getUserRepresentation().getId())
                .roles()
                .realmLevel()
                .listAll()
                .stream()
                .map(RoleRepresentation::getName)
                // default-roles-meditrack is keycloak internal, users shouldn't see it
                .filter(role -> !role.equals("default-roles-meditrack"))
                .toList();
    }

    @Mapping(source = "dto", target = "userRepresentation", qualifiedByName = "fromDtoToUserRepresentation")
    public abstract User fromDto(UserDto dto);

    @Named("fromDtoToUserRepresentation")
    protected UserRepresentation fromDtoToUserRepresentation(UserDto dto) {
        UserRepresentation userRepresentation = new UserRepresentation();

        if (dto.id() == null) {
            // id is only null on creation
            userRepresentation.setEnabled(true);
        } else {
            userRepresentation.setId(String.valueOf(dto.id()));
        }
        userRepresentation.setUsername(dto.username());
        userRepresentation.setEmail(dto.email());
        userRepresentation.setFirstName(dto.firstName());
        userRepresentation.setLastName(dto.lastName());

        if (dto.password() != null) {
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setValue(dto.password());
            userRepresentation.setCredentials(List.of(credentialRepresentation));
        }

        userRepresentation.setRealmRoles(dto.roles());


        return userRepresentation;
    }

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<UserDto> toDtoList(List<User> users);
}
