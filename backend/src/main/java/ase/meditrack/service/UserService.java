package ase.meditrack.service;

import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@Service
@Slf4j
public class UserService {
    private final RealmResource meditrackRealm;
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(RealmResource meditrackRealm, UserRepository repository, UserMapper mapper) {
        this.meditrackRealm = meditrackRealm;
        this.repository = repository;
        this.mapper = mapper;
    }

    @PostConstruct
    public void createAdminUser() {
        if (meditrackRealm.users().count() == 0) {
            log.info("Creating default admin user...");
            this.create(defaultAdminUser());
        }
    }

    /**
     * Fetches all users from the database and matches additional attributes from keycloak.
     *
     * @return List of all users
     */
    public List<User> findAll() {
        return repository.findAll()
                .stream()
                .peek(u -> u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation()))
                .toList();
    }

    /**
     * Fetches a user by id from the database and matches additional attributes from keycloak.
     *
     * @param id, the id of the user
     * @return the user
     */
    public User findById(UUID id) {
        return repository.findById(id).map(u -> {
            u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation());
            return u;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a user in the database and in keycloak.
     *
     * @param user, the user to create
     * @return the created user
     */
    public User create(User user) {
        UserRepresentation userRepresentation = createKeycloakUser(user.getUserRepresentation());
        user.setId(UUID.fromString(userRepresentation.getId()));
        user = repository.save(user);
        //as transient ignores the userRepresentation, we need to set it again
        user.setUserRepresentation(userRepresentation);
        return user;
    }

    private UserRepresentation createKeycloakUser(UserRepresentation userRepresentation) {
        try (Response response = meditrackRealm.users().create(userRepresentation)) {
            if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                log.error("Error creating user: {}", response.getStatusInfo().getReasonPhrase());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String id = CreatedResponseUtil.getCreatedId(response);
            setUserRoles(meditrackRealm, id, userRepresentation.getRealmRoles());
            return meditrackRealm.users().get(id).toRepresentation();
        }
    }

    /**
     * Updates a user in the database and in keycloak.
     *
     * @param user, the user to update
     * @return the updated user
     */
    public User update(User user) {
        meditrackRealm.users().get(user.getUserRepresentation().getId()).update(user.getUserRepresentation());
        setUserRoles(meditrackRealm,
                user.getUserRepresentation().getId(), user.getUserRepresentation().getRealmRoles());
        //as transient ignores the userRepresentation, we need to remember and set it again if we want to return it
        UserRepresentation userRepresentation = user.getUserRepresentation();

        //perform partial update: load user from db and update only the fields that are not null
        user = updateChangedAttributes(user);
        user = repository.save(user);
        user.setUserRepresentation(userRepresentation);
        return user;
    }

    /**
     * Deletes a user from the database and from keycloak.
     *
     * @param id, the id of the user to delete
     */
    public void delete(UUID id) {
        try (Response response = meditrackRealm.users().delete(String.valueOf(id))) {
            if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                log.error("Error deleting user: {}", response.getStatusInfo().getReasonPhrase());
                if (response.getStatusInfo().getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            repository.deleteById(id);
        }
    }

    private static void setUserRoles(RealmResource meditrackRealm, String userId, List<String> roles) {
        if (roles == null) return;
        // for some reason keycloak doesn't use the roles in UserRepresentation, so we need to set them explicitly
        List<RoleRepresentation> userRoles = roles.stream().map(role -> meditrackRealm.roles().get(role).toRepresentation()).toList();
        UserResource user = meditrackRealm.users().get(userId);
        RoleScopeResource roleScopeResource = user.roles().realmLevel();
        roleScopeResource.remove(roleScopeResource.listAll());
        user.roles().realmLevel().add(userRoles);
    }

    private User defaultAdminUser() {
        UserDto user = new UserDto(
                null,
                "admin",
                "admin",
                "admin@meditrack.com",
                "admin",
                "admin",
                List.of("admin"),
                null,
                1.0f,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return mapper.fromDto(user);
    }

    private User updateChangedAttributes(User user) {
        User dbUser = repository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.getRole() != null) {
            dbUser.setRole(user.getRole());
        }
        if (user.getWorkingHoursPercentage() != null) {
            dbUser.setWorkingHoursPercentage(user.getWorkingHoursPercentage());
        }
        if (user.getCurrentOverTime() != null) {
            dbUser.setCurrentOverTime(user.getCurrentOverTime());
        }
        if (user.getSpecialSkills() != null) {
            dbUser.setSpecialSkills(user.getSpecialSkills());
        }
        if (user.getTeam() != null) {
            dbUser.setTeam(user.getTeam());
        }
        if (user.getHolidays() != null) {
            dbUser.setHolidays(user.getHolidays());
        }
        if (user.getPreferences() != null) {
            dbUser.setPreferences(user.getPreferences());
        }
        if (user.getRequestedShiftSwaps() != null) {
            dbUser.setRequestedShiftSwaps(user.getRequestedShiftSwaps());
        }
        if (user.getSuggestedShiftSwaps() != null) {
            dbUser.setSuggestedShiftSwaps(user.getSuggestedShiftSwaps());
        }
        if (user.getShifts() != null) {
            dbUser.setShifts(user.getShifts());
        }
        if (user.getCanWorkShiftTypes() != null) {
            dbUser.setCanWorkShiftTypes(user.getCanWorkShiftTypes());
        }
        if (user.getPreferredShiftTypes() != null) {
            dbUser.setPreferredShiftTypes(user.getPreferredShiftTypes());
        }

        return dbUser;
    }
}
