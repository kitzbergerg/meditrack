package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MockUserDto;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.Role;
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
    public UserDto toDto(User user) {
        return new UserDto(
                UUID.fromString(user.getUserRepresentation().getId()),
                user.getUserRepresentation().getUsername(),
                null,
                user.getUserRepresentation().getEmail(),
                user.getUserRepresentation().getFirstName(),
                user.getUserRepresentation().getLastName(),
                // for some reason keycloak doesn't return the roles in UserRepresentation, so we need to fetch them manually
                meditrackRealm.users()
                        .get(user.getUserRepresentation().getId())
                        .roles()
                        .realmLevel()
                        .listAll()
                        .stream()
                        .map(RoleRepresentation::getName)
                        // default-roles-meditrack is keycloak internal, users shouldn't see it
                        .filter(role -> !role.equals("default-roles-meditrack"))
                        .toList(),
                user.getRole() != null ? user.getRole().getId() : null,
                user.getWorkingHoursPercentage(),
                user.getCurrentOverTime(),
                user.getSpecialSkills(),
                user.getTeam() != null ? user.getTeam().getId() : null,
                user.getHolidays() != null ? user.getHolidays().stream().map(Holiday::getId).toList() : null,
                user.getPreferences() != null ? user.getPreferences().getId() : null,
                user.getRequestedShiftSwaps() != null
                        ? user.getRequestedShiftSwaps().stream().map(ShiftSwap::getId).toList() : null,
                user.getSuggestedShiftSwaps() != null
                        ? user.getSuggestedShiftSwaps().stream().map(ShiftSwap::getId).toList() : null,
                user.getShifts() != null ? user.getShifts().stream().map(Shift::getId).toList() : null,
                user.getCanWorkShiftTypes() != null
                        ? user.getCanWorkShiftTypes().stream().map(ShiftType::getId).toList() : null,
                user.getPreferredShiftTypes() != null
                        ? user.getPreferredShiftTypes().stream().map(ShiftType::getId).toList() : null
        );
    }

    // TODO: Remove mockDtos
    public MockUserDto toMockedUserDto(User user) {
        return new MockUserDto(user.getId());
    }

    public MockUserDto toMockedUserDto(List<User> user) {
        return new MockUserDto(user.get(0).getId());
    }

    public UserDto map(List<User> value) {
        return toDto(value.get(0));
    }

    public User fromDto(UserDto dto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        User user = new User();

        if (dto.id() == null) {
            // id is only null on creation
            userRepresentation.setEnabled(true);
        } else {
            userRepresentation.setId(String.valueOf(dto.id()));
            user.setId(dto.id());
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

        user.setUserRepresentation(userRepresentation);

        user.setWorkingHoursPercentage(dto.workingHoursPercentage());
        user.setCurrentOverTime(dto.currentOverTime());
        user.setSpecialSkills(dto.specialSkills());

        if (dto.role() != null) {
            Role role = new Role();
            role.setId(dto.role());
            user.setRole(role);
        }

        if (dto.team() != null) {
            Team team = new Team();
            team.setId(dto.team());
            user.setTeam(team);
        }

        if (dto.holidays() != null) {
            user.setHolidays(dto.holidays().stream().map(id -> {
                Holiday holiday = new Holiday();
                holiday.setId(id);
                return holiday;
            }).toList());
        }

        if (dto.preferences() != null) {
            Preferences preferences = new Preferences();
            preferences.setId(dto.preferences());
            user.setPreferences(preferences);
        }

        if (dto.requestedShiftSwaps() != null) {
            user.setRequestedShiftSwaps(dto.requestedShiftSwaps().stream().map(id -> {
                ShiftSwap shiftSwap = new ShiftSwap();
                shiftSwap.setId(id);
                return shiftSwap;
            }).toList());
        }

        if (dto.suggestedShiftSwaps() != null) {
            user.setSuggestedShiftSwaps(dto.suggestedShiftSwaps().stream().map(id -> {
                ShiftSwap shiftSwap = new ShiftSwap();
                shiftSwap.setId(id);
                return shiftSwap;
            }).toList());
        }

        if (dto.shifts() != null) {
            user.setShifts(dto.shifts().stream().map(id -> {
                Shift shift = new Shift();
                shift.setId(id);
                return shift;
            }).toList());
        }

        if (dto.canWorkShiftTypes() != null) {
            user.setCanWorkShiftTypes(dto.canWorkShiftTypes().stream().map(id -> {
                ShiftType shiftType = new ShiftType();
                shiftType.setId(id);
                return shiftType;
            }).toList());
        }

        if (dto.preferredShiftTypes() != null) {
            user.setPreferredShiftTypes(dto.preferredShiftTypes().stream().map(id -> {
                ShiftType shiftType = new ShiftType();
                shiftType.setId(id);
                return shiftType;
            }).toList());
        }

        return user;
    }

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<UserDto> toDtoList(List<User> users);
}
