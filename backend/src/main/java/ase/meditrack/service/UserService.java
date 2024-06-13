package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.UserValidator;
import ase.meditrack.model.entity.MonthlyWorkDetails;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyWorkDetailsRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import ase.meditrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@Service
@Slf4j
public class UserService {
    private final RealmResource meditrackRealm;
    private final UserRepository repository;
    private final UserValidator userValidator;
    private final ShiftTypeRepository shiftTypeRepository;
    private final MonthlyWorkDetailsRepository monthlyWorkDetailsRepository;

    public UserService(RealmResource meditrackRealm, UserRepository repository, UserValidator userValidator,
                       ShiftTypeRepository shiftTypeRepository,
                       MonthlyWorkDetailsRepository monthlyWorkDetailsRepository) {
        this.meditrackRealm = meditrackRealm;
        this.repository = repository;
        this.userValidator = userValidator;
        this.shiftTypeRepository = shiftTypeRepository;
        this.monthlyWorkDetailsRepository = monthlyWorkDetailsRepository;
    }

    private static void setUserRoles(RealmResource meditrackRealm, String userId, List<String> roles) {
        if (roles == null) return;
        // for some reason keycloak doesn't use the roles in UserRepresentation, so we need to set them explicitly
        List<RoleRepresentation> userRoles =
                roles.stream().map(role -> meditrackRealm.roles().get(role).toRepresentation()).toList();
        UserResource user = meditrackRealm.users().get(userId);
        RoleScopeResource roleScopeResource = user.roles().realmLevel();
        roleScopeResource.remove(roleScopeResource.listAll());
        user.roles().realmLevel().add(userRoles);
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
     * Fetches all users from the team of the dm from the database and matches additional attributes from keycloak.
     *
     * @param principal the current user's id
     * @return List of all users from the team of the dm
     */
    public List<User> findByTeam(Principal principal) throws NoSuchElementException {
        UUID dmId = UUID.fromString(principal.getName());
        Optional<User> dm = repository.findById(dmId);
        if (dm.isEmpty()) {
            throw new NotFoundException("User doesnt exist");
        }
        if (dm.get().getTeam() == null) {
            throw new NotFoundException("User has no team");
        }
        return repository.findAllByTeam(dm.get().getTeam()).stream()
                .peek(u -> u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation()))
                .toList();
    }

    /**
     * Fetches a user by id from the database and matches additional attributes from keycloak.
     *
     * @param id the id of the user
     * @return the user
     */
    public User findById(UUID id) {
        return repository.findById(id).map(u -> {
            u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation());
            return u;
        }).orElseThrow(() -> new NotFoundException("Could not find user with id: " + id + "!"));
    }

    /**
     * Creates a user in the database and in keycloak.
     *
     * @param user the user to create
     * @return the created user
     */
    public User create(User user) {
        UserRepresentation userRepresentation = createKeycloakUser(user.getUserRepresentation());
        user.setId(UUID.fromString(userRepresentation.getId()));
        user.setCurrentOverTime(0);
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
     * @param user      the user to update
     * @param principal the current user's id
     * @return the updated user
     */
    public User update(User user, Principal principal) {
        //checks if employee to delete is part of team
        this.userValidator.teamValidate(user.getId(), principal);
        meditrackRealm.users().get(user.getUserRepresentation().getId()).update(user.getUserRepresentation());
        setUserRoles(meditrackRealm,
                user.getUserRepresentation().getId(), user.getUserRepresentation().getRealmRoles());
        //as transient ignores the userRepresentation, we need to remember and set it again if we want to return it
        UserRepresentation userRepresentation = user.getUserRepresentation();

        //perform partial update: load user from db and update only the fields that are not null
        user = updateChangedAttributes(user);
        user = repository.save(user);

        user.setUserRepresentation(userRepresentation);
        UUID id = user.getId();
        return repository.findById(user.getId()).map(u -> {
            u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation());
            log.info("Updating user {}.", u);
            return u;
        }).orElseThrow(() -> new NotFoundException("Could not find user with id: " + id + "!"));
    }

    /**
     * Deletes a user from the database and from keycloak.
     *
     * @param id        the id of the user to delete
     * @param principal the current user's id
     */
    public void delete(UUID id, Principal principal) {
        //checks if employee to delete is part of dms team
        this.userValidator.teamValidate(id, principal);
        try (Response response = meditrackRealm.users().delete(String.valueOf(id))) {
            if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                log.error("Error deleting user: {}", response.getStatusInfo().getReasonPhrase());
                if (response.getStatusInfo().getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    throw new NotFoundException("Could not find user with id: " + id + "!");
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            repository.deleteById(id);
        }
    }

    @Transactional
    protected User updateChangedAttributes(User user) {
        User dbUser = repository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Could not find user with id: " + user.getId() + "!"));

        if (user.getRole() != null && user.getRole().getId() != null) {
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
            for (ShiftType shiftType : dbUser.getCanWorkShiftTypes()) {
                shiftType.getWorkUsers().remove(dbUser);
            }
            dbUser.getCanWorkShiftTypes().clear();

            // Add new shift types
            for (ShiftType newShiftType : user.getCanWorkShiftTypes()) {
                ShiftType shiftType = shiftTypeRepository.findById(newShiftType.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("ShiftType not found"));
                dbUser.getCanWorkShiftTypes().add(shiftType);
                shiftType.getWorkUsers().add(dbUser);
            }
        }
        if (user.getPreferredShiftTypes() != null) {
            dbUser.setPreferredShiftTypes(user.getPreferredShiftTypes());
        }

        return dbUser;
    }

    /**
     * Fetches the user with the id from the principal.
     *
     * @param principal the current user
     * @return user with the id from principal
     */
    public User getPrincipalWithTeam(Principal principal) {
        UUID dmId = UUID.fromString(principal.getName());
        Optional<User> dm = repository.findById(dmId);
        if (dm.isEmpty()) {
            throw new NotFoundException("User doesnt exist");
        }
        if (dm.get().getTeam() == null) {
            throw new NotFoundException("User has no team");
        }
        return dm.get();
    }

    /**
     * Fetches work details from the principal, given a month and year.
     *
     * @param userId of user
     * @param month of the work details
     * @param year of the work details
     * @return monthly work details for the user, given the month and year
     */
    public MonthlyWorkDetails findWorkDetailsByIdAndMonthAndYear(UUID userId, Month month, Year year) {
        MonthlyWorkDetails details = monthlyWorkDetailsRepository.findMonthlyWorkDetailsByUserIdAndMonthAndYear(
                userId, month.getValue(), year.getValue());
        return details;
    }
}
