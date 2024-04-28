package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.UserDto;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Mapper
public abstract class UserMapper {

    @Autowired
    private RealmResource meditrackRealm;

    @Named("toDto")
    public UserDto toDto(UserRepresentation user) {
        return new UserDto(
                UUID.fromString(user.getId()),
                user.getUsername(),
                null,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                // for some reason keycloak doesn't return the roles in UserRepresentation, so we need to fetch them manually
                meditrackRealm.users()
                        .get(user.getId())
                        .roles()
                        .realmLevel()
                        .listAll()
                        .stream()
                        .map(RoleRepresentation::getName)
                        // default-roles-meditrack is keycloak internal, users shouldn't see it
                        .filter(role -> !role.equals("default-roles-meditrack"))
                        .toList()
        );
    }

    public UserRepresentation fromDto(UserDto dto) {
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
    public abstract List<UserDto> toDtoList(List<UserRepresentation> entityList);
}
